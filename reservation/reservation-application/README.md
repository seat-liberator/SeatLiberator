# Reservation Application

`reservation:reservation-application` 은 좌석 관리, 예약 생성/변경/취소, 빈자리 알림 신청/취소, 그리고 예약 취소 이후 알림 이벤트 발행을 담당하는 Spring Boot
애플리케이션입니다.

## 빌드 구성

- plugin: `seatliberator.resource-application`
- 직접 의존성: `:reservation:reservation-api`
- 외부 API 의존성: `:notification:notification-api`

resource-server 보안, JPA, 공통 웹 설정은 bootstrap starter가 맡고, 이 모듈은 reservation/waitlist 도메인 흐름과 notification 이벤트 연계만 직접 가집니다.

## 패키지 구조

- `reservation.book`: 좌석, 예약 유스케이스와 REST API
- `reservation.waitlist`: 대기열 유스케이스와 REST API
- `reservation.verification`: 예약 조회/검증용 내부 포트
- `reservation.shared`: 좌석 식별자, 시간 범위, 공통 설정
- `reservation.bootstrap.seed`: 로컬/개발용 시드 데이터

## 실행 설정

기본 프로필 구조:

- [application.yml](/home/lilamaris/IdeaProjects/SeatLiberator/reservation/reservation-application/src/main/resources/application.yml)
- [application-local.yml](/home/lilamaris/IdeaProjects/SeatLiberator/reservation/reservation-application/src/main/resources/application-local.yml)
- [application-dev.yml](/home/lilamaris/IdeaProjects/SeatLiberator/reservation/reservation-application/src/main/resources/application-dev.yml)

핵심 설정:

- `seatliberator.resource-server.security.authorize.jwk-set-uri`
- `spring.datasource.*`
- `spring.jpa.*`
- `app.seed.enabled`

## REST API

### Seat

- `POST /seat`
- `PUT /seat`
- `DELETE /seat/{roomId}/{seatId}`

### Reservation

- `POST /reservation`
- `PUT /reservation`
- `DELETE /reservation`

현재 사용자 식별자는 요청 본문이 아니라 `ActorContextHolder` 의 actor subject 에서 가져옵니다.

### Waitlist

- `POST /waitlist`
- `DELETE /waitlist/{waitlistId}`

## 핵심 정책

- 한 사용자는 동시에 하나의 예약만 가질 수 있습니다.
- 같은 좌석의 예약 시간은 서로 겹칠 수 없습니다.
- 시간 범위는 `[start, end)` 규칙으로 처리합니다.
- 모든 REST API는 인증이 필요합니다.
- 빈자리 알림은 동일한 활성 요청을 중복 등록할 수 없습니다.

## 이벤트 연계

예약 취소 후 notification 모듈과는 직접 호출이 아니라 이벤트로 연결됩니다.

흐름:

1. 예약이 취소되면 `ReservationCanceled` 이벤트가 발생합니다.
2. 빈자리 알림 요청 중 같은 좌석이고 시간이 겹치는 `ACTIVE` 요청만 조회합니다.
3. 각 요청에 대해 `NOTIFICATION_CREATE_REQUEST` 이벤트를 발행합니다.
4. 처리된 요청은 `FULFILLED` 상태로 전이됩니다.

상세 시나리오는 기존 문서인 `docs/VACANCY_ALERT_CANCEL_FLOW.md` 를 참고하면 됩니다.

## 테스트

통합
테스트는 [ReservationIntegrationTest.java](/home/lilamaris/IdeaProjects/SeatLiberator/reservation/reservation-application/src/test/java/com/seatliberator/seatliberator/reservation/integration/ReservationIntegrationTest.java)
와 관련 구성에서 H2, 고정 Clock, 테스트용 `JwtDecoder`, 이벤트 캡처용 `EventPublisher` 를 사용합니다.
