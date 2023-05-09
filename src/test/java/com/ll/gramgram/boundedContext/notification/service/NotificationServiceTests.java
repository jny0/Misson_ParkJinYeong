package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.TestUt;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class NotificationServiceTests {
    @Autowired
    private MemberService memberService;
    @Autowired
    private LikeablePersonService likeablePersonService;
    @Autowired
    private NotificationService notificationService;

    @Test
    @DisplayName("호감 표시를 하면 알림 생성")
    void t001() throws Exception {
        Member memberUser2 = memberService.findByUsername("user2").orElseThrow();
        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();

        // member의 username 기준 : user2 -> user3 에게 호감표시
        // 인스타 아이디 기준 : insta_user2 -> insta_user3 에게 호감표시
        likeablePersonService.like(memberUser2, "insta_user3", 1);

        // user3 에게 전송된 알림들 모두 가져오기
        List<Notification> notifications = notificationService.findByToInstaMember(memberUser3.getInstaMember());

        // 그중에 최신 알림 가져오기
        Notification lastNotification = notifications.get(0);

        // 보낸이의 인스타 아이디가 insta_user2 인지 체크
        assertThat(lastNotification.getFromInstaMember().getUsername()).isEqualTo("insta_user2");
        // 알림의 사유가 LIKE 인지 체크
        assertThat(lastNotification.getTypeCode()).isEqualTo("LIKE");
        // 알림내용 중에서 새 호감사유코드가 1인지 체크
        assertThat(lastNotification.getNewAttractiveTypeCode()).isEqualTo(1);
    }

    @Test
    @DisplayName("호감사유를 수정하면 그에 따른 알림이 생성된다.")
    void t002() throws Exception {
        Member memberUser2 = memberService.findByUsername("user2").orElseThrow();
        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();

        LikeablePerson likeablePerson = likeablePersonService.like(memberUser2, "insta_user3", 1).getData();
        TestUt.setFieldValue(likeablePerson, "modifyUnlockDate", LocalDateTime.now().minusSeconds(1));
        likeablePersonService.modifyAttractive(memberUser2, "insta_user3", 2);

        List<Notification> notifications = notificationService.findByToInstaMember(memberUser3.getInstaMember());

        Notification lastNotification = notifications.get(0);

        assertThat(lastNotification.getFromInstaMember().getUsername()).isEqualTo("insta_user2");
        assertThat(lastNotification.getTypeCode()).isEqualTo("MODIFY_ATTRACTIVE_TYPE");
        assertThat(lastNotification.getPreviousAttractiveTypeCode()).isEqualTo(1);
        assertThat(lastNotification.getNewAttractiveTypeCode()).isEqualTo(2);
    }
}