# [스프링 시큐리티] 스프링 시큐리티 & JWT
### 환경설정
- 스프링 5.3.23 </br>
- 스프링 부트 2.7.4 </br>
- 자바 17 </br>
- 스프링 시큐리티 5.7.3 </br>
- MySql 8.0.36 </br>
- JPA 2.7.3 </br>

### 준비 과정
- 스프링 부트3 버전에서는 시큐리티 6사용 되므로 2로 다운그레이

### 시큐리티 사용 이유
- 인증 
- 권한 부여
- CSRF 보호: 웹 애플리케이션 취약점을 악용하여 사이트 간 요청 위조 공격을 수행하는 것을 방지하는 보안 조치

### 시큐리티 작동 방식
- 요청을 가로채서 보호된 자원에 대한 액세스를 허용하기 전에 보안 검사를 수행
- <img src="img/img_1.png" width="730"/>
### JWT(JSON Web Token) 사용 이유
- 서버의 확장성이 높으며 대량의 트래픽이 발생해도 대처할 수 있음
- 특정 DB/서버에 의존하지 않아도 인증할 수 있음

### JWT 구성, 방식
- 헤더 Header: 토큰 타입, 암호화 알고리즘 명시
- 페이로드 Payload: JWT에 넣을 데이터, JWT 발급, 만료일 등 명시
- 시그니처 Signature: 헤더, 페이로드가 변조 되었는지를 확인하는 역할
- 토큰 발급: 많은 사용자의 인증 방식을 저장하고 있지 않기 때문에, 가볍고 확장성이 좋음
- 세션 발급: 무겁지만, 그만큼 보안성이 좋은 방식

### 참고
- SecurityConfig.java
  - WebSecurityConfigurerAdapter 지원 중단(5.7 이상)-> SecurityFilterChain 빈등록으로 해결
  - authorizeRequests 대체 권고(5.6.1 이상) -> authorizeHttpRequests 
    - / https://whatistudy.tistory.com/entry/%EC%B6%94%EA%B0%80-AuthorizeRequests-vs-AuthorizeHttpRequests / https://velog.io/@csh0034/Spring-Security-Config-Refactoring /
- 카카오 로그인
  - client-authentication-method: POST 필요
  - ProviderId 캐스팅 수정 필요
- 깃 yml 파일 이그노어시 반영 안될때 캐시 삭제 필요
  - git rm -r --cached 
  - git add .
  - git commit -m "-"
- Refresh Token 인증 과정
- <img src="img/img.jpg" width="730"/>
- - (1) 사용자가 ID , PW를 통해 로그인
  - (2) 서버에서는 회원 DB에서 값을 비교
  - (3-4) 로그인이 완료되면 Access Token, Refresh Token을 발급. 이때 회원DB에도 Refresh Token을 저장 
  - (5) 사용자는 Refresh Token은 안전한 저장소에 저장 후, Access Token을 헤더에 실어 요청
  - (6-7) Access Token을 검증하여 이에 맞는 데이터를 보냄
  - (8) 시간이 지나 Access Token이 만료되면,
  - (9) 사용자는 이전과 동일하게 Access Token을 헤더에 실어 요청을 보냄
  - (10-11) 서버는 Access Token이 만료됨을 확인하고 권한없음을 신호로 보냄
  - (12) 사용자는 Refresh Token과 Access Token을 함께 서버로 보냄
  - (13) 서버는 받은 Access Token이 조작되지 않았는지 확인한 후, Refresh Token과 사용자의 DB에 저장되어 있던 Refresh Token을 비교. Token이 동일하고 유효기간도 지나지 않았다면 새로운 Access Token을 발급 
  - (14) 서버는 새로운 Access Token을 헤더에 실어 다시 API 요청 응답을 진행
- Tip
  - Access Token 만료가 될 때마다 계속 과정 9~11을 거칠 필요는 없다. 사용자(프론트엔드)에서 Access Token의 Payload를 통해 유효기간을 알 수 있다. 따라서 프론트엔드 단에서 API 요청 전에 토큰이 만료됐다면 곧바로 재발급 요청을 할 수도 있다.
- https://tansfil.tistory.com/59