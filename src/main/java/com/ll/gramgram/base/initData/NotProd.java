package com.ll.gramgram.base.initData;

import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
                Member memberUser100 = memberService.join("user100", "1234").getData();

                Member memberUser5ByKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__%s\".formatted(kakaoDevUserOAuthId)").getData();
                Member memberUser6ByGoogle = memberService.whenSocialLogin("GOOGLE", "GOOGLE__%s\".formatted(googleDevUserOAuthId)").getData();
                Member memberUser7ByNaver = memberService.whenSocialLogin("NAVER", "NAVER__%s\".formatted(naverDevUserOAuthId)").getData();
                Member memberUser8ByFacebook = memberService.whenSocialLogin("FACEBOOK", "FACEBOOK__%s\".formatted(facebookDevUserOAuthId)").getData();

                instaMemberService.connect(memberUser2, "insta_user2", "M");
                instaMemberService.connect(memberUser3, "insta_user3", "W");
                instaMemberService.connect(memberUser4, "insta_user4", "M");

                likeablePersonService.like(memberUser3, "insta_user4", 1);
                likeablePersonService.like(memberUser3, "insta_user100", 2);
            }
        };
    }
}
