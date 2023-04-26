## [2WeekFix] 박진영

### 중간 리팩토링 및 기능 개발

---  

- **호감 취소 기능 관련 리팩토링**
  - [x] 변수명 `delete`에서 `cancel` 변경
  - [x] Controller에서의 유효성 및 권한체크로직을 Service로 변경
  - [x] Controller에서 Service를 이용해서 권한체크
  - [x] 유효성 및 권한체크 로직 분리 (`canCancel`)
  - [x] 호감 취소 시 양방향 관계 고려 (호감 취소한 사람 / 취소된 사람에게 알려줌)
###

- **호감 표시 기능 관련 리팩토링**
  - [x] 변수명 `add`에서 `like`로 변경
  - [x] `canLike` 생성 -> 유효성 및 권한 체크 로직 분리
  - [x] `canModifyAttractive` 생성 -> 매력포인트 수정 로직 분리
  - [x] Test에서 JPA를 사용해서 조회
  - [X] LikeablePersonServiceTests 도입
###
- [x] **BaseEntity** 추가하여 모든 엔티티들이 가져야할 기본적인 필드에 대한 중복 제거
- [x] 테스트 환경에서는 MySQL이 아닌 H2를 메모리모드로 사용


### 
- **UI 디자인**
  - [x] 인스타 계정 등록 페이지
    - [x] 계정 확인 URL 추가
  - [x] 호감 표시 등록 페이지 
    - [x] 계정 확인 URL 추가
  - [x] 호감 목록 페이지
  - [ ] 내 정보 페이지
  - [ ] 404 페이지 커스터마이징


