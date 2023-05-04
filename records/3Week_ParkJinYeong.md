## [3Week] 박진영

### 미션 요구사항 분석 & 체크리스트

---  

**[사전작업]**
- 페이스북 로그인
- 인스타 인증
  - 스프링 시큐리티를 개조하여 인스타 아이디 얻기
  - 인스타그램 Instagram Basic Display API 를 이용한 계정연결
  - 카카오/네이버/구글 로그인 redirect url에 https적용
- NotProd.java 에 있던 소셜 로그인 관련 내용 application-secret.yml 로 옮김


###
**[필수과제]** 네이버 클라우드 플랫폼을 통한 배포
- [x] https://www.codelike.jny0.xyz 도메인 설정
- [x] 로그인 및 기타 기능 사용 가능


###
**[필수과제]** 호감 사유 수정/취소 쿨타임 3시간

- [x] 호감 표시 한 후 3시간 동안 사유 변경/취소 불가
- [x] 호감 사유 변경 후 3시간 동안 사유 변경/취소 불가

###
**[선택과제]** 알림 기능 구현
- [x] 호감 표시 받은 경우 알림페이지에서 확인
- [x] 알림 생성 시 readDate = null
- [ ] 본인에 대한 호감사유 변경 시 알림페이지에서 확인 가능
- [ ] 알림 확인 시 readDate = 현재날짜

###
### 3주차 미션 요약

---  

**[접근 방법]**


1. 호감 사유 변경 / 취소 쿨타임
- AppConfig에서 application.yml에 있는 쿨타임 정보 가져오기 
  - `AppConfig.getLikeablePersonModifyCoolTime()` 구현
- LikeablePerson에서 `modifyUnlockDate` 생성
  - 호감 표시 할 때 표시 시간으로 지정
  - 호감 사유 변경 시 변경 시간으로 갱신
- 쿨타임 동안은 수정/취소 불가
  - 쿨타임 동안 호감 사유 변경 / 취소 버튼 비활성화
  - `LikeablePersonService::canCancel`, `LikeablePersonService::canModify` 에 쿨타임 체크 로직 추가
  - 쿨타임이 지날 때 까지 얼마나 남았는지 "hh시간 m분" 형태로 표현

###
2. 네이버 클라우드 플랫폼을 통한 배포

- 네이버 클라우드 플랫폼 포트포워딩으로 리눅스 접속용 포트 설정
- MariaDB 설치 및 gram__prod DB 생성
- git 설치 및 클론
- gradlew 를 소유자가 실행 가능한 상태로 변경, 빌드, 실행
- npm 설치 및 관리 콘솔에서 도메인 추가
- Dockerfile 생성, gram 이미지 생성, 이미지 실행
- 배포 후 리다이렉션 URL 추가



**MySQL 연동 오류**
```bash
java.sql.sqlinvalidauthorizationspecexception 
  : Access denied for user 'rot'@'centos7' (using password: YES)
````
mariadb 접속이 되지 않는 오류가 나와 확인해보니 외부에서 mariadb로 접근할 때 권한이 없어서 발생한 문제였다.
mariadb에서 접속 권한을 설정해주었더니 해결되었다.


**리눅스 thymeleaf 에러(Error resolving template)**
- 인텔리제이에서는 이상 없이 html파일을 찾아서 동작하지만 리눅스환경에서 jar로 실행하면 다음과 같은 에러 발생
```bash
2023-05-03T14:29:57.716+09:00 ERROR 24327 --- [nio-8080-exec-1] org.thymeleaf.TemplateEngine             
: [THYMELEAF][http-nio-8080-exec-1] Exception processing template "/usr/home/main": Error resolving template [/usr/home/main], template might not exist or might not be accessible by any of the configured Template Resolvers
```
검색해본 결과 Controller에서 return하여 html 파일을 불러오는 경로 문제여서 아래와 같이 수정해주었다.
 - Controller에서 return 경로에 / 를 제거 
   - `return "/usr/home/main";"` => `return "usr/home/main";`
 - html 파일에서 / 제거 
   - `<html layout:decorate="~{/usr/layout/layout.html}">` => `<html layout:decorate="~{usr/layout/layout.html}">`

위와 같이 수정한 결과 리눅스 환경에서도 잘 실행되었다.

배포 완료
https://www.codelike.jny0.xyz 


3. 알림 기능 구현
- LikeablePersonService::like 에서 나에 대한 호감 등록될 때 

###
**[특이사항]**
- 배포 과정 중에 오류를 만나 해결하는데 시간이 꽤 걸려서 선택미션을 구현하지 못한 점이 아쉽다.
리팩토링 시간에 추가적으로 구현할 예정이다.