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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if ( member.hasConnectedInstaMember() == false ) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember fromInstaMember = member.getInstaMember();
        List<LikeablePerson> fromlikeablePeople = fromInstaMember.getFromLikeablePeople();

        LikeablePerson fromlikeablePerson = fromlikeablePeople.stream()
                .filter(e->e.getToInstaMember().getUsername().equals(username))
                .findFirst()
                .orElse(null);

        // 중복 체크
        if(fromlikeablePerson != null){
            if (fromlikeablePerson.getAttractiveTypeCode() != attractiveTypeCode) {
                String previousDisplayName = fromlikeablePerson.getAttractiveTypeDisplayName(); // 이전에 등록된 매력
                fromlikeablePerson.updateAttractiveTypeCode(attractiveTypeCode); // 새로 입력된 매력으로 수정

                return RsData.of("S-2", "호감 상대(%s)의 매력을 \"%s\"에서 \"%s\"(으)로 수정했습니다."
                        .formatted(fromlikeablePerson.getToInstaMemberUsername(), previousDisplayName, fromlikeablePerson.getAttractiveTypeDisplayName()));
            }
            return RsData.of("F-3", "이미 등록된 상대입니다.");
        }

        long likeablePersonFromMax = AppConfig.getLikeablePersonFromMax(); // 설정파일의 최대 호감표시 가능 개수
        if (fromlikeablePeople.size() >= likeablePersonFromMax) {
            return RsData.of("F-", "호감 상대는 %d명까지만 등록할 수 있습니다.".formatted(likeablePersonFromMax));
        }


        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        // 내가 좋아하는 상대
        fromInstaMember.addFromLikeablePerson(likeablePerson);

        // 나를 좋아하는 상대
        toInstaMember.addToLikeablePerson(likeablePerson);

        return RsData.of("S-1", "입력하신 인스타유저(%s)가 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }


    public Optional<LikeablePerson> findById(Long Id) {
        return likeablePersonRepository.findById(Id);
    }

    @Transactional
    public RsData<LikeablePerson> delete(LikeablePerson likeablePerson) {
        likeablePersonRepository.delete(likeablePerson);
        return RsData.of("S-1", "호감 상대(%s)를 삭제했습니다.".formatted(likeablePerson.getToInstaMemberUsername()));
    }

//    @Transactional
//    public RsData<LikeablePerson> modify(LikeablePerson likeablePerson, int attractiveTypeCode) {
//        String previousAttractiveTypeDisplayName = likeablePerson.getAttractiveTypeDisplayName(); // 이전에 등록된 매력
//        likeablePerson.setAttractiveTypeCode(attractiveTypeCode); // 새로 입력된 매력으로 수정
//        likeablePersonRepository.save(likeablePerson);
//        return RsData.of("S-2", "호감 상대(%s)의 매력을 \"%s\"에서 \"%s\"(으)로 수정했습니다."
//                .formatted(likeablePerson.getToInstaMemberUsername(), previousAttractiveTypeDisplayName, likeablePerson.getAttractiveTypeDisplayName()));
//    }


}
