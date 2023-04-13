## [2Week] 박진영

### 미션 요구사항 분석 & 체크리스트

---  

**[필수과제]** 호감 표시 할 때 예외처리 케이스 3가지
- [x] 호감 상대는 10명까지만 등록 가능하다
  - [x] 11명부터는 등록 처리가 안되며 rq.historyBack으로 예외 처리
- [x] 이미 호감 표시한 상대를 같은 매력 포인트로 다시 등록할 수 없다.
  - [x] 이를 시도할 경우 등록 처리가 안되며 rq.historyBack으로 예외 처리
- [x] 이미 호감 표시한 상대를 다른 매력 포인트로 등록할 경우 성공 처리한다.
  - [x] 이 때 새 호감 상대로 등록하지 않고, 기존 호감 표시에서 사유만 수정된다.
  - [x] resultCode=S-2 / 수정메시지 표시

**[선택과제]** 네이버 로그인 기능
- [x] 네이버 API 가져오기
- [x] 네이버를 통한 회원가입
- [x] 네이버를 통한 로그인 처리
  - [x] 네이버 로그인 시 메인에 user id만 표시
#

### 2주차 미션 요약

---  

**[접근 방법]**
1. 호감표시는 최대 10개까지만 가능
- **application.yml** 파일에 최대 호감표시 가능 개수 입력
  - AppConfig를 통해 application.yml에 접근하여 최대 개수 받이오기
- likeablePeople.size()가 AppConfig에서 받아온 getLikeablePersonFromMax()와 같거나 크다면 더 이상 등록 불가 (historyBack)
- 최대로 입력된 상태에서도 매력 수정은 가능해야하기 때문에 중복 검사보다 늦게 검사
- 테스트 케이스로 해당 기능 확인(**LikeablePersonControllerTests**)
  - t008 : 호감 상대 10개 입력 후 11번째 부터는 등록불가
###
2. 중복된 상대에게 호감표시 불가 및 매력포인트가 다르다면 수정하기
- for문을 사용하여 호감 상대 목록 중에 입력된 인스타 계정과 중복된 상대가 있는지 확인
- 중복된 상대가 있다면 매력포인트도 같은지 확인
  - 매력포인트가 같다면 중복 대상이므로 등록 불가 (historyBack)
  - 매력포인트가 다르다면 **LikeablePersonService의** **modify**메서드 실행 (S-2, redirect)
- 테스트 케이스로 해당 기능 확인(**LikeablePersonControllerTests**)
  - t009 : 이미 등록한 상대, 매력포인트가 같을 경우 등록 불가
  - t010 : 이미 등록한 상대, 매력포인트가 다를 경우 매력 수정  
###
3. 네이버 소셜 로그인
- **application-config.yml** 파일에 naver-client-id와 naver-client-secret 추가
  - **.gitignore**로 노출 방지
- **application.yml**에 정보 추가
- **CustomOAuth2UserService**에서 네이버 로그인시 id만 추출하여 username에 표시 (참고 : https://lotuus.tistory.com/80)

##
**[특이사항]**
- 예외처리 케이스를 service와 controller 둘 중 어디에서 구현해야할 지 고민이 많았는데 일단 controller에 구현하였다.
- 중복된 상대를 검사할 때 for문이 아니라 stream을 사용하면 더 간단하게 구현할 수 있을 것 같아서 리팩토링 시 진행해보려고 한다.
- 네이버 로그인에서 일단 id만 추출하여 사용했는데, 추후에 다른 정보들도 쉽게 가져다 사용할 수 있게 user 정보를 UserInfo클래스로 분리하는 것이 좋을 것 같다.


---
[리팩토링]
- [x] controller에서 구현한 예외처리 로직을 service로 옮기기
- [x] 수정 로직에서 매력포인트 수정 후 자동 저장되므로 `save` 삭제
- [x] 캡슐화를 위해 setter 사용 지양
- [x] 이미 등록된 상대인지 검색하는 로직을 for문 사용에서 stream을 사용하는 코드로 수정