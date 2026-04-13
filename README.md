# SeatLiberator

SeatLiberator 는 좌석 예약과 빈자리 알림, 게시판, 사용자 인증을 여러 Spring Boot 애플리케이션으로 나눠 구성한 멀티모듈 프로젝트입니다.

핵심 의도는 두 가지입니다.

- 도메인 API 와 애플리케이션 bootstrap 경계를 분리한다.
- 애플리케이션 타입별 공통 runtime bootstrap 을 모듈과 convention plugin 으로 정리한다.

## 기술 스택

- Java 21
- Gradle multi-project build
- Spring Boot
- Spring Security / OAuth2 Resource Server / OAuth2 Client
- Spring Data JPA
- PostgreSQL, H2

## 저장소 구조

### 애플리케이션 모듈

- `identity:identity-application`
  사용자 가입, 로그인, federated login, JWT 발급, JWKS 공개
- `reservation:reservation-application`
  좌석 관리, 예약, 빈자리 알림 신청/취소
- `board:board-application`
  게시판, 카테고리, 게시글 관리와 board 권한 규칙
- `notification:notification-application`
  사용자 알림 조회와 알림 생성 이벤트 소비

### 도메인/API 모듈

- `identity:identity-api`
- `board:board-api`
- `reservation:reservation-api`
- `notification:notification-api`

이 모듈들은 애플리케이션 간 명시적 API 의존성 경계를 표현합니다.

### 공통 모듈

- `kernel`
  공통 기반 타입
- `event-relay:*`
  애플리케이션 이벤트 relay 지원
- `idempotency:idempotency-core`
  idempotency 관련 공통 로직
- `identity:identity-client`
  resource server 앱에서 actor / role / introspection 재사용 지원

### bootstrap / build logic

- `bootstrap:web-application-starter`
  공통 웹/JPA bootstrap
- `bootstrap:resource-application-starter`
  resource server 보안 bootstrap
- `build-include`
  애플리케이션 타입별 Gradle convention plugin

관련 문서:

- [bootstrap/README.md](/home/lilamaris/IdeaProjects/SeatLiberator/bootstrap/README.md)
- [build-include/README.md](/home/lilamaris/IdeaProjects/SeatLiberator/build-include/README.md)

## 애플리케이션 타입

현재 애플리케이션은 크게 두 타입으로 나뉩니다.

- `seatliberator.web-application`
  일반 웹 애플리케이션 bootstrap
- `seatliberator.resource-application`
  JWT resource server 계열 애플리케이션 bootstrap

resource server 계열 앱은 공통 보안 기본값을 bootstrap 에서 받고, 앱 모듈은 필요한 matcher 규칙만 추가합니다.

대표 설정 prefix:

- `seatliberator.application.*`
- `seatliberator.resource-server.security.*`
- `seatliberator.resource-server.security.authorize.*`

## 빌드와 테스트

전체 빌드:

```sh
./gradlew build
```

특정 모듈 테스트:

```sh
./gradlew :board:board-application:test
./gradlew :reservation:reservation-application:test
```

특정 애플리케이션 실행:

```sh
./gradlew :identity:identity-application:bootRun
./gradlew :reservation:reservation-application:bootRun
./gradlew :board:board-application:bootRun
./gradlew :notification:notification-application:bootRun
```

기본적으로 각 앱은 `application.yml` 에서 활성 프로필을 `local` 로 두고, 세부 설정은 `application-local.yml`, `application-dev.yml` 등에서 관리합니다.

## 모듈별 문서

- [identity/identity-application/README.md](/home/lilamaris/IdeaProjects/SeatLiberator/identity/identity-application/README.md)
- [reservation/reservation-application/README.md](/home/lilamaris/IdeaProjects/SeatLiberator/reservation/reservation-application/README.md)
- [board/board-application/README.md](/home/lilamaris/IdeaProjects/SeatLiberator/board/board-application/README.md)
- [notification/notification-application/README.md](/home/lilamaris/IdeaProjects/SeatLiberator/notification/notification-application/README.md)
- [identity/identity-client/README.md](/home/lilamaris/IdeaProjects/SeatLiberator/identity/identity-client/README.md)

## 개발 방향

- 도메인 의미가 있는 의존성은 각 API / 애플리케이션 모듈이 직접 표현합니다.
- 공통 runtime 설정은 bootstrap 모듈에서 제공합니다.
- build logic 와 runtime bootstrap 은 같은 애플리케이션 타입 축을 따릅니다.
- 앱별 보안 정책은 bootstrap 기본값 위에 필요한 규칙만 추가하는 방향을 우선합니다.
