## [2Week] 박진영

### 중간 리팩토링 및 기능 개발

---  

- [x] 호감 취소 기능 관련 리팩토링
  - [x] 변수명 `delete`에서 `cancel` 변경
  - [x] Controller에서의 유효성 및 권한체크로직을 Service로 변경
  - [x] Controller에서 Service를 이용해서 권한체크
  - [x] 유효성 및 권한체크 로직 분리 (`canCancel`)

- [x] BaseEntity 추가하여 모든 엔티티들이 가져야할 기본적인 필드에 대한 중복 제거
