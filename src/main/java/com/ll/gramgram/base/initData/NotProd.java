package com.ll.gramgram.base.initData;

import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import com.ll.gramgram.standard.util.Ut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
@Profile({"dev", "test"})
public class NotProd {

    @Value("${custom.security.oauth2.client.registration.kakao.devUserOauthId}")
    private String kakaoDevUserOAuthId;

    @Value("${custom.security.oauth2.client.registration.naver.devUserOauthId}")
    private String naverDevUserOAuthId;

    @Value("${custom.security.oauth2.client.registration.google.devUserOauthId}")
    private String googleDevUserOAuthId;

    @Value("${custom.security.oauth2.client.registration.facebook.devUserOauthId}")
    private String facebookDevUserOAuthId;

    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            InstaMemberService instaMemberService,
            LikeablePersonService likeablePersonService
    ) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                Member memberAdmin = memberService.join("admin", "1234").getData();
                Member memberUser1 = memberService.join("user1", "1234").getData();
                Member memberUser2 = memberService.join("user2", "1234").getData();
                Member memberUser3 = memberService.join("user3", "1234").getData();
                Member memberUser4 = memberService.join("user4", "1234").getData();
                Member memberUser5 = memberService.join("user5", "1234").getData();
                Member memberUser100 = memberService.join("user100", "1234").getData();
                Member memberUser101 = memberService.join("user101", "1234").getData();

                //Member memberUser5ByKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__%s\".formatted(kakaoDevUserOAuthId)").getData();
                Member memberUser5ByKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__2733201280").getData();

                Member memberUser6ByGoogle = memberService.whenSocialLogin("GOOGLE", "GOOGLE__%s\".formatted(googleDevUserOAuthId)").getData();
                Member memberUser7ByNaver = memberService.whenSocialLogin("NAVER", "NAVER__%s\".formatted(naverDevUserOAuthId)").getData();
                Member memberUser8ByFacebook = memberService.whenSocialLogin("FACEBOOK", "FACEBOOK__%s\".formatted(facebookDevUserOAuthId)").getData();

                instaMemberService.connect(memberUser2, "insta_user2", "M");
                instaMemberService.connect(memberUser3, "insta_user3", "W");
                instaMemberService.connect(memberUser4, "insta_user4", "M");
                instaMemberService.connect(memberUser5, "insta_user5", "W");
                instaMemberService.connect(memberUser101, "insta_user101", "M");
                instaMemberService.connect(memberUser5ByKakao, "insta_kakao", "M");

                // 원활한 테스트와 개발을 위해서 자동으로 만들어지는 호감이 삭제, 수정이 가능하도록 쿨타임해제
                LikeablePerson likeablePersonToinstaUser4From3 = likeablePersonService.like(memberUser3, "insta_user4", 1).getData();
                Ut.reflection.setFieldValue(likeablePersonToinstaUser4From3, "modifyUnlockDate", LocalDateTime.now().minusSeconds(1));

                LikeablePerson likeablePersonToinstaUser100 = likeablePersonService.like(memberUser3, "insta_user100", 2).getData();
                Ut.reflection.setFieldValue(likeablePersonToinstaUser100, "modifyUnlockDate", LocalDateTime.now().minusSeconds(1));

                LikeablePerson likeablePersonToinstaUser3 = likeablePersonService.like(memberUser4, "insta_user3", 1).getData();
                Ut.reflection.setFieldValue(likeablePersonToinstaUser3, "modifyUnlockDate", LocalDateTime.now().minusSeconds(1));

                // 필터링 테스트용
                LikeablePerson likeablePersonToinstaUser4From2 = likeablePersonService.like(memberUser2, "insta_user4", 2).getData();
                LikeablePerson likeablePersonToinstaUser4From5 = likeablePersonService.like(memberUser5, "insta_user4", 3).getData();
                LikeablePerson likeablePersonToinstaUser4From101 = likeablePersonService.like(memberUser101, "insta_user4", 1).getData();
            }
        };
    }
}
