# Identity Application

`identity:identity-application` 은 사용자 가입과 인증, federated login, JWT 발급, JWKS 공개를 담당하는 Spring Boot 애플리케이션입니다.

## 빌드 구성

- plugin: `seatliberator.web-application`
- 직접 의존성: `:identity:identity-api`
- 추가 보안 의존성: `spring-boot-starter-oauth2-client`
- 외부 API 의존성: `:reservation:reservation-api`, `:board:board-api`

이 모듈은 resource server 앱이 아니라 OAuth2 client 기반 로그인 흐름을 직접 구성합니다.

## 주요 역할

- credential 기반 회원가입 / 로그인
- GitHub, Google 기반 federated login
- access token / refresh token 발급
- 공개키 목록(JWKS) 제공
- 기본 namespace role 부여 등록

## 실행 설정

프로필 파일:

- [application.yml](/home/lilamaris/IdeaProjects/SeatLiberator/identity/identity-application/src/main/resources/application.yml)
- [application-local.yml](/home/lilamaris/IdeaProjects/SeatLiberator/identity/identity-application/src/main/resources/application-local.yml)
- [application-dev.yml](/home/lilamaris/IdeaProjects/SeatLiberator/identity/identity-application/src/main/resources/application-dev.yml)

핵심 설정 prefix:

- `identity.jwks.*`
- `identity.jwt.*`
- `identity.application.auth.credential.*`
- `identity.application.security.*`
- `spring.security.oauth2.client.registration.github.*`
- `spring.security.oauth2.client.registration.google.*`
- `spring.datasource.*`

`application.yml` 에서는 JWKS 서명 키와 OAuth2 client registration 기본 설정을 관리합니다.

## 주요 엔드포인트

### Credential 인증

- `POST /auth/sign-up`
- `POST /auth/sign-in`

성공 시 access token / refresh token 을 반환합니다.

### Federated 인증

- `/oauth2/**`
- `/login/**`

GitHub, Google 로그인 이후 내부 사용자와 연계한 뒤 동일하게 토큰을 발급합니다.

### JWKS

- `GET /.well-known/jwks.json`

현재
구현은 [JwksController.java](/home/lilamaris/IdeaProjects/SeatLiberator/identity/identity-application/src/main/java/com/seatliberator/seatliberator/jwks/infrastructure/web/controller/JwksController.java)
에서 공개키 목록을 제공합니다.

## 키 관리

JWKS 키 생성
스크립트는 [keyGen.sh](/home/lilamaris/IdeaProjects/SeatLiberator/identity/identity-application/src/main/resources/keyGen.sh)
에 있습니다.

사용 예시:

```sh
sh keyGen.sh key-20260309-120000
```

생성된 키 디렉터리는 `identity.jwks.keys` 설정에 등록해서 사용합니다.

## 보안 구성

이 모듈의 보안은 bootstrap resource-server starter에 의존하지 않고, identity 전용 `SecurityFilterChain` 들을 직접 구성합니다. 따라서 credential 인증,
federated 인증, CORS 정책, 토큰 발급 흐름은 모두 이 모듈 내부 보안 설정이 책임집니다.
