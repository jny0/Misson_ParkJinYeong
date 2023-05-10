package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.base.event.EventAfterModifyAttractiveType;
import com.ll.gramgram.base.event.EventBeforeCancelLike;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public RsData<LikeablePerson> like(Member actor, String username, int attractiveTypeCode) {

        RsData canLikeRsData = canLike(actor, username, attractiveTypeCode);

        if (canLikeRsData.isFail()) return canLikeRsData;
        if (canLikeRsData.getResultCode().equals("S-2")) return modifyAttractive(actor, username, attractiveTypeCode);

        InstaMember fromInstaMember = actor.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(actor.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .modifyUnlockDate(AppConfig.genLikeablePersonModifyUnlockDate())
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        // 내가 좋아하는 상대
        fromInstaMember.likeFromLikeablePerson(likeablePerson);

        // 나를 좋아하는 상대
        toInstaMember.likeToLikeablePerson(likeablePerson);

        publisher.publishEvent(new EventAfterLike(this, likeablePerson));

        return RsData.of("S-1", "입력하신 상대(%s)에 대한 호감 표시가 등록되었습니다.".formatted(username), likeablePerson);
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }


    public Optional<LikeablePerson> findById(Long Id) {
        return likeablePersonRepository.findById(Id);
    }

    @Transactional
    public RsData<LikeablePerson> cancel(LikeablePerson likeablePerson) {
        publisher.publishEvent(new EventBeforeCancelLike(this, likeablePerson));

        // 내가 한 호감 표시 사라짐
        likeablePerson.getFromInstaMember().removeFromLikeablePerson(likeablePerson);
        // 누군가가 나에 대해 한 호감 표시 사라짐
        likeablePerson.getToInstaMember().removeToLikeablePerson(likeablePerson);

        likeablePersonRepository.delete(likeablePerson);
        String likeCanceledUsername = likeablePerson.getToInstaMember().getUsername();
        return RsData.of("S-1", "해당 상대(%s)에 대한 호감을 취소했습니다.".formatted(likeCanceledUsername));
    }

    public RsData canCancel(Member actor, LikeablePerson likeablePerson) {
        if (likeablePerson == null) {
            return RsData.of("F-1:", "이미 취소된 항목입니다");
        }

        long actorInstaMemberId = actor.getInstaMember().getId(); // 삭제하는 사람의 인스타계정 번호
        long fromInstaMemberId = likeablePerson.getFromInstaMember().getId(); // 삭제 대상을 등록한 사람의 인스타계정 번호

        // 삭제 항목 소유권 체크
        if (actorInstaMemberId != fromInstaMemberId) {
            return RsData.of("F-2", "취소 권한이 없습니다.");
        }

        if(!likeablePerson.isModifyUnlocked()){
            return RsData.of("F-3", "취소 가능 시간이 아닙니다.");
        }

        return RsData.of("S-1", "취소 가능합니다.");
    }

    private RsData canLike(Member actor, String username, int attractiveTypeCode) {
        if (!actor.hasConnectedInstaMember()) {
            return RsData.of("F-1", "먼저 본인의 인스타그램 계정을 입력해야 합니다.");
        }

        InstaMember fromInstaMember = actor.getInstaMember();
        if (fromInstaMember.getUsername().equals(username)) {
            return RsData.of("F-2", "본인을 호감상대로 등록할 수 없습니다.");
        }

        List<LikeablePerson> fromLikeablePeople = fromInstaMember.getFromLikeablePeople();

        LikeablePerson fromLikeablePerson = fromLikeablePeople.stream()
                .filter(e -> e.getToInstaMember().getUsername().equals(username))
                .findFirst()
                .orElse(null);

        // 중복 체크
        if (fromLikeablePerson != null) {
            if (fromLikeablePerson.getAttractiveTypeCode() != attractiveTypeCode) {
                return RsData.of("S-2", "%s님에 대해서 호감 표시가 가능합니다.".formatted(username));
            }
            return RsData.of("F-3", "이미 등록된 상대입니다.");
        }

        long likeablePersonFromMax = AppConfig.getLikeablePersonFromMax(); // 설정파일의 최대 호감표시 가능 개수
        if (fromLikeablePeople.size() >= likeablePersonFromMax) {
            return RsData.of("F-4", "호감 상대는 %d명까지만 등록할 수 있습니다.".formatted(likeablePersonFromMax));
        }

        return RsData.of("S-1", "%s님에 대해서 호감 표시가 가능합니다.".formatted(username));
    }

    @Transactional
    public RsData<LikeablePerson> modifyAttractive(Member actor, Long id, int attractiveTypeCode) {
        Optional<LikeablePerson> likeablePersonOptional = findById(id);

        if (likeablePersonOptional.isEmpty()) {
            return RsData.of("F-1", "존재하지 않는 호감표시입니다.");
        }

        LikeablePerson likeablePerson = likeablePersonOptional.get();

        return modifyAttractive(actor, likeablePerson, attractiveTypeCode);
    }

    @Transactional
    public RsData<LikeablePerson> modifyAttractive(Member actor, LikeablePerson likeablePerson, int attractiveTypeCode) {
        RsData canModifyRsData = canModifyLike(actor, likeablePerson);

        if (canModifyRsData.isFail()) {
            return canModifyRsData;
        }

        String previousAttractiveTypeDisplayName = likeablePerson.getAttractiveTypeDisplayName();
        String username = likeablePerson.getToInstaMember().getUsername();

        modifyAttractionTypeCode(likeablePerson, attractiveTypeCode);

        String newAttractiveTypeDisplayName = likeablePerson.getAttractiveTypeDisplayName();

        return RsData.of("S-3", "%s님에 대한 호감사유를 %s에서 %s(으)로 변경합니다.".formatted(username, previousAttractiveTypeDisplayName, newAttractiveTypeDisplayName), likeablePerson);
    }

    @Transactional
    public RsData<LikeablePerson> modifyAttractive(Member actor, String username, int attractiveTypeCode)  {

        List<LikeablePerson> fromLikeablePeople = actor.getInstaMember().getFromLikeablePeople();

        LikeablePerson fromLikeablePerson = fromLikeablePeople
                .stream()
                .filter(e -> e.getToInstaMember().getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (fromLikeablePerson == null) {
            return RsData.of("F-7", "호감표시를 하지 않았습니다.");
        }

        return modifyAttractive(actor, fromLikeablePerson, attractiveTypeCode);
    }

    private void modifyAttractionTypeCode(LikeablePerson likeablePerson, int attractiveTypeCode) {
        int previousAttractiveTypeCode = likeablePerson.getAttractiveTypeCode();
        RsData rsData = likeablePerson.updateAttractiveTypeCode(attractiveTypeCode);

        if (rsData.isSuccess()) {
            publisher.publishEvent(new EventAfterModifyAttractiveType(this, likeablePerson, previousAttractiveTypeCode, attractiveTypeCode));
        }
    }

    public RsData canModifyLike(Member actor, LikeablePerson likeablePerson) {
        if (!actor.hasConnectedInstaMember()) {
            return RsData.of("F-1", "먼저 본인의 인스타그램 계정을 입력해야 합니다.");
        }

        InstaMember fromInstaMember = actor.getInstaMember();

        if (!Objects.equals(likeablePerson.getFromInstaMember().getId(), fromInstaMember.getId())) {
            return RsData.of("F-2", "해당 호감 사유를 변경할 권한이 없습니다.");
        }

        if(!likeablePerson.isModifyUnlocked()){
            return RsData.of("F-3", "변경 가능 시간이 아닙니다.");
        }

        return RsData.of("S-1", "호감 표시 변경이 가능합니다.");
    }

    public Optional<LikeablePerson> findByFromInstaMember_usernameAndToInstaMember_username(String fromInstaMemberUsername, String toInstaMemberUsername) {
        return likeablePersonRepository.findByFromInstaMember_usernameAndToInstaMember_username(fromInstaMemberUsername, toInstaMemberUsername);
    }

    public List<LikeablePerson> filterLikeablePeople(List<LikeablePerson> likeablePeople, String gender, int attractiveTypeCode) {

        List<LikeablePerson> filteredItems = likeablePeople;


        if (gender != null) {
            filteredItems = filteredItems.stream()
                    .filter(item -> item.getFromInstaMember().getGender().equals(gender))
                    .collect(Collectors.toList());
        }

        if (attractiveTypeCode != 0) {
            filteredItems = filteredItems.stream()
                    .filter(item -> item.getAttractiveTypeCode() == attractiveTypeCode)
                    .collect(Collectors.toList());
        }

        return filteredItems;
    }
}
