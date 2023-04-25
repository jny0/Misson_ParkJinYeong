package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member actor, String username, int attractiveTypeCode) {

        RsData canLikeRsData = canLike(actor, username, attractiveTypeCode);

        if(canLikeRsData.isFail()) return canLikeRsData;
        if(canLikeRsData.getResultCode().equals("S-2")) return modifyAttractive(actor,username, attractiveTypeCode);

        InstaMember fromInstaMember = actor.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(actor.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        // 내가 좋아하는 상대
        fromInstaMember.likeFromLikeablePerson(likeablePerson);

        // 나를 좋아하는 상대
        toInstaMember.likeToLikeablePerson(likeablePerson);

        return RsData.of("S-1", "입력하신 인스타유저(%s)가 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }


    public Optional<LikeablePerson> findById(Long Id) {
        return likeablePersonRepository.findById(Id);
    }

    @Transactional
    public RsData<LikeablePerson> cancel(LikeablePerson likeablePerson) {
        String toInstaMemberUsername = likeablePerson.getToInstaMember().getUsername();
        likeablePersonRepository.delete(likeablePerson);

        // 내가 한 호감 표시 사라짐
        likeablePerson.getFromInstaMember().removeFromLikeablePerson(likeablePerson);
        // 누군가가 나에 대해 한 호감 표시 사라짐
        likeablePerson.getToInstaMember().removeToLikeablePerson(likeablePerson);

        String likeCanceledUsername = likeablePerson.getToInstaMember().getUsername();
        return RsData.of("S-1", "호감 상대(%s)를 삭제했습니다.".formatted(likeCanceledUsername));
    }

    public RsData canCancel(Member actor, LikeablePerson likeablePerson) {
        if (likeablePerson == null) {
            return RsData.of("F-1:", "이미 삭제된 항목입니다");
        }

        long actorInstaMemberId = actor.getInstaMember().getId(); // 삭제하는 사람의 인스타계정 번호
        long fromInstaMemberId = likeablePerson.getFromInstaMember().getId(); // 삭제 대상을 등록한 사람의 인스타계정 번호

        // 삭제 항목 소유권 체크
        if (actorInstaMemberId != fromInstaMemberId) {
            return RsData.of("F-2", "삭제 권한이 없습니다.");
        }

        return RsData.of("S-1", "삭제 가능합니다.");
    }

    private RsData canLike(Member actor, String username, int attractiveTypeCode){
        if (!actor.hasConnectedInstaMember()) {
            return RsData.of("F-1", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
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
        if (fromLikeablePerson != null ) {
            if (fromLikeablePerson.getAttractiveTypeCode() != attractiveTypeCode) {
                return RsData.of("S-2", "%s님에 대해서 호감표시가 가능합니다.".formatted(username));
            }
            return RsData.of("F-3", "이미 등록된 상대입니다.");
        }

        long likeablePersonFromMax = AppConfig.getLikeablePersonFromMax(); // 설정파일의 최대 호감표시 가능 개수
        if (fromLikeablePeople.size() >= likeablePersonFromMax) {
            return RsData.of("F-4", "호감 상대는 %d명까지만 등록할 수 있습니다.".formatted(likeablePersonFromMax));
        }

        return RsData.of("S-1", "%s님에 대해서 호감표시가 가능합니다.".formatted(username));
    }

    private RsData<LikeablePerson> modifyAttractive(Member member, String username, int attractiveTypeCode) {

        List<LikeablePerson> fromLikeablePeople = member.getInstaMember().getFromLikeablePeople();

        LikeablePerson fromLikeablePerson = fromLikeablePeople
                .stream()
                .filter(e -> e.getToInstaMember().getUsername().equals(username))
                .findFirst()
                .orElse(null);


        String previousAttractiveTypeDisplayName = fromLikeablePerson.getAttractiveTypeDisplayName(); // 이전에 등록된 매력
        fromLikeablePerson.updateAttractiveTypeCode(attractiveTypeCode); // 새로 입력된 매력으로 수정
        likeablePersonRepository.save(fromLikeablePerson);

        return RsData.of("S-2", "호감 상대(%s)의 매력을 \"%s\"에서 \"%s\"(으)로 수정했습니다."
                .formatted(username, previousAttractiveTypeDisplayName, fromLikeablePerson.getAttractiveTypeDisplayName()));
    }

    public Optional<LikeablePerson> findByFromInstaMember_usernameAndToInstaMember_username(String fromInstaMemberUsername, String toInstaMemberUsername) {
        return likeablePersonRepository.findByFromInstaMember_usernameAndToInstaMember_username(fromInstaMemberUsername, toInstaMemberUsername);
    }

}
