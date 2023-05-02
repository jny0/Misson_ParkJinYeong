## [3Week] 박진영

### 미션 요구사항 분석 & 체크리스트

---  

[사전작업]
- [x] 페이스북 로그인
- [x] 인스타 인증
  - [x] 스프링 시큐리티를 개조하여 인스타 아이디 얻기
  - [x] 인스타그램 Instagram Basic Display API 를 이용한 계정연결
  - [x] 카카오/네이버/구글 로그인 redirect url에 https적용
- [x] NotProd.java 에 있던 소셜 로그인 관련 내용 application-secret.yml 로 옮김


###
**[필수과제]** 네이버 클라우드 플랫폼을 통한 배포



###
**[필수과제]** 호감 삭제/표시 쿨타임 3시간
- [x] 설정정보 가져오기
- [x] 호감 표시 후 modifyUnlockDate 갱신(현재날짜 + 쿨타임)
- [x] 호감사유변경 시에 modifyUnlockDate 갱신(현재날짜 + 쿨타임)
- [x] 쿨타임동안은 수정/삭제 불가
- [ ] LikeablePersonService::canDelete, LikeablePersonService::canLike 에 쿨타임 체크 추가

###
**[선택과제]** 알림 기능 구현



### 3주차 미션 요약

---  

**[접근 방법]**

1. 네이버 클라우드 플랫폼을 통한 배포


**[특이사항]**
