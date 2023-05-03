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
- [x] 네이버 클라우드 플랫폼 포트포워딩으로 리눅스 접속용 포트 설정
- [x] MariaDB 설치 및 gram__prod DB 생성
- [x] git 설치 및 클론
- [x] gradlew 를 소유자가 실행 가능한 상태로 변경, 빌드, 실행
- [x] npm 설치 및 관리 콘솔에서 도메인 추가
- [x] Dockerfile 생성, gram 이미지 생성, 이미지 실행
- [x] 배포 후 리다이렉션 URL 추가

###
**[필수과제]** 호감 삭제/표시 쿨타임 3시간
- [x] 설정정보 가져오기
- [x] 호감 표시 후 modifyUnlockDate 갱신(현재날짜 + 쿨타임)
- [x] 호감사유변경 시에 modifyUnlockDate 갱신(현재날짜 + 쿨타임)
- [x] 쿨타임동안은 수정/삭제 불가
- [x] LikeablePersonService::canCancel, LikeablePersonService::canModify 에 쿨타임 체크 추가

###
**[선택과제]** 알림 기능 구현
- [ ] 호감 표시 받은 경우 알림페이지에서 확인
- [ ] 알림 생성 시 readDate = null
- [ ] 본인에 대한 호감사유 변경 시 알림페이지에서 확인 가능
- [ ] 알림 확인 시 readDate = 현재날짜


### 3주차 미션 요약

---  

**[접근 방법]**

1. 네이버 클라우드 플랫폼을 통한 배포

**리눅스 thymeleaf 에러(Error resolving template)**
- 인텔리제이에서는 이상 없이 html파일을 찾아서 동작하지만 리눅스환경에서 jar로 실행하면 다음과 같은 에러 발생
```bash
2023-05-03T14:29:57.716+09:00 ERROR 24327 --- [nio-8080-exec-1] org.thymeleaf.TemplateEngine             
: [THYMELEAF][http-nio-8080-exec-1] Exception processing template "/usr/home/main": Error resolving template [/usr/home/main], template might not exist or might not be accessible by any of the configured Template Resolvers
```
검색해본 결과 Controller에서 return하여 html 파일을 불러오는 경로 문제
 - Controller에서 return 경로에 / 를 제거 
   - `return "/usr/home/main";"` => `  return "usr/home/main";`
 - html 파일에서 / 제거 
   - `<html layout:decorate="~{/usr/layout/layout.html}">` => `<html layout:decorate="~{usr/layout/layout.html}">`

위와 같이 수정한 결과 리눅스 환경에서도 잘 실행되었다.


**[특이사항]**
배포 과정 중에 오류를 만나 해결하는데 시간이 꽤 걸려서 선택미션을 구현하지 못한 점이 아쉽다.
리팩토링 시간에 추가적으로 구현할 예정이다.