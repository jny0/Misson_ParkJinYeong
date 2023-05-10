## [4Week] 박진영

### 미션 요구사항 분석 & 체크리스트

---  

**[사전작업]**
- 번역 작업


###
**[필수과제]** 네이버클라우드플랫폼을 통한 배포, 도메인, HTTPS 까지 적용
- [x] https://www.codelike.jny0.xyz 에서 접속 가능
- [x] 운영서버에서 각종 소셜로그인, 인스타 아이디 연결이 잘 되야함


###
**[필수과제]** 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 성별 필터링기능 구현
- [x] UI 작업
- [x] 특정 성별을 가진 사람에게서 받은 호감만 필터링해서 볼 수 있음

###
**[선택과제]** 젠킨스를 통해서 리포지터리의 main 브랜치에 커밋 이벤트가 발생하면 자동으로 배포가 진행되도록

###
**[선택과제]**  내가 받은 호감리스트(/usr/likeablePerson/toList)에서 호감 사유 필터링 기능
- [x] 특정 호감사유에 대한 호감만 필터링해서 볼 수 있음


###
**[선택과제]**  내가 받은 호감리스트(/usr/likeablePerson/toList)에서 정렬기능
- [x] 최신순(기본)
    - 가장 최근에 받은 호감표시를 우선적으로 표시
- [x] 날짜순
  - 가장 오래전에 받은 호감표시를 우선적으로 표시
- [x] 인기 많은 순
    - 가장 인기가 많은 사람들의 호감표시를 우선적으로 표시
- [x] 인기 적은 순
    - 가장 인기가 적은 사람들의 호감표시를 우선적으로 표시
- [x] 성별순
    - 여성에게 받은 호감표시를 먼저 표시하고, 그 다음 남자에게 받은 호감표시를 후에 표시
  - [x] 2순위 정렬조건으로는 최신순
- [x] 호감사유순
  - 외모 때문에 받은 호감표시를 먼저 표시하고, 그 다음 성격 때문에 받은 호감표시를 후에 표시, 마지막으로 능력 때문에 받은 호감표시를 후에 표시
  - [x] 2순위 정렬조건으로는 최신순


###
### 4주차 미션 요약

---  

**[접근 방법]**
1. toList에서 성별 및 호감사유 필터링
- `@RequestParam` 어노테이션을 사용하여 `gender`, `attractiveTypeCode`,`sortCode` 파라미터를 받아옴
- `Controller`에서 인스타 인증되어있는지 확인하고 `Service`의 `filterList`에서 필터링 구현
- `stream.filter()`를 사용하여 성별과 호감사유 필터링
- `stream.sorted()`를 사용하여 정렬
  - 최신순 (기본) : `LikeablePerson::getCreateDate`를 기준으로 오름차순
  - 날짜순 (오래된 순) : `LikeablePerson::getCreateDate`를 기준으로 내림차순
  - 인기 많은 순 : `likablePerson.getFromInstaMember().getToLikeablePeople().size()` 를 기준으로 내림차순
  - 인기 적은 순 : `likablePerson.getFromInstaMember().getToLikeablePeople().size()` 를 기준으로 내림차순
  - 성별 순 + 최신 순 : 최신순으로 먼저 정렬 후 `likablePerson.getFromInstaMember().getGender` 를 기준으로 내림차순
  - 호감 사유 순 + 최신 순 : 최신순으로 먼저 정렬 후 `LikeablePerson::getAttractiveTypeCode` 를 기준으로 오름차순
- 필터링과 정렬에 대한 테스트 케이스 추가

❓성별을 선택하지 않고 다른 기준만 선택했을 때 필터링되지 않는 오류 
- 성별 기준에서 "전체"를 선택하면 , value 값이 빈 문자열("")로 전달되도록 설정되어있기 때문
- `!gender.isEmpty()` 조건을 추가하여 필터링이 정상적으로 작동되도록 수정


###
**[특이사항]**
- 젠킨스를 사용한 자동배포는 구현하지 못했다.