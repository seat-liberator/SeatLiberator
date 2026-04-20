# Reservation Application

`reservation:reservation-application`은 좌석 관리, 예약, 좌석 가용성 조회, 좌석 점유 상태 조회, 대기열 요청을 처리하는 Spring Boot 애플리케이션 모듈입니다.

## 모듈 개요

이 모듈은 reservation 도메인의 애플리케이션 계층과 인프라 어댑터를 포함합니다.

- 좌석 생성/수정/삭제
- 예약 생성/수정/취소/조회
- 특정 시간 범위의 가용 좌석 조회
- 특정 시간 범위의 좌석별 점유 상태 조회
- 대기열 요청 생성/취소 및 예약 취소/만료 이벤트 기반 대기열 처리

도메인 엔티티와 값 객체는 `reservation:reservation-domain`에 있고, 외부에 노출되는 API 타입은 `reservation:reservation-api`와 이 모듈의 웹 요청/응답 타입을 함께 사용합니다.

## 빌드 구성

`build.gradle.kts` 기준 구성은 다음과 같습니다.

- plugin: `seatliberator.resource-application`
- API 의존성: `:reservation:reservation-api`
- Domain 의존성: `:reservation:reservation-domain`
- 테스트 fixture 의존성: `testFixtures(project(":reservation:reservation-domain"))`
- 외부 API 의존성: `:notification:notification-api`
- JPA 테스트 의존성: `org.springframework.boot:spring-boot-starter-data-jpa-test`

모듈 단위 테스트는 다음 명령으로 실행합니다.

```bash
./gradlew :reservation:reservation-application:test
```

## 패키지 구조

주요 패키지는 기능 단위로 나뉩니다.

- `reservation.seat`: 좌석 관리 유스케이스, 포트, JPA 어댑터, 웹 컨트롤러
- `reservation.book`: 예약 생성/수정/취소/조회, 예약 정책, 예약 조회 criteria, JPA 어댑터, 웹 컨트롤러
- `reservation.availability`: 가용 좌석 조회와 좌석별 점유 상태 조회
- `reservation.waitlist`: 대기열 요청, 대기열 승격, 빈자리 이벤트 처리, JPA 어댑터, 웹 컨트롤러
- `reservation.verification`: 예약 사용 검증용 내부 유스케이스와 정책 엔진
- `reservation.shared`: 공통 예외, 보안, JPA specification, seed, 웹 advice, notification 연계

각 기능 패키지는 대체로 다음 구조를 따릅니다.

- `application/model`: 애플리케이션 계층 모델
- `application/port/in`: 유스케이스 입력 포트와 command/query/result
- `application/port/out`: 저장소/조회 포트와 조회 criteria
- `application/service`: 유스케이스 구현
- `infrastructure/persistence`: JPA 기반 persistence adapter
- `infrastructure/web`: REST controller와 request 타입

## 실행 설정

설정 파일은 `src/main/resources` 아래에 있습니다.

- `application.yml`: 기본 profile과 포트 설정
- `application-local.yml`: 로컬 실행용 H2 datasource, JWK set URI, actuator 설정
- `application-dev.yml`: 개발 환경 datasource, JWK set URI, JPA validate 설정

주요 환경 변수는 다음과 같습니다.

- `PORT`: 애플리케이션 포트, 기본값 `8082`
- `PROFILE`: Spring profile, 기본값 `local`
- `LOCAL_JWKS_BASE_URL`, `JWKS_BASE_URL`: resource server JWK set base URL
- `LOCAL_DB_URL`, `LOCAL_DB_USERNAME`, `LOCAL_DB_PASSWORD`: local profile datasource 설정
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DIALECT`: dev profile datasource/JPA 설정
- `app.seed.enabled`: local profile에서 시드 데이터 적재 여부

## Endpoint 구성

Gateway 경유 시 외부 경로는 `/api/v1` prefix가 붙고, 이 모듈의 컨트롤러는 아래 경로를 제공합니다.

### Seat

- `POST /seat`
- `PUT /seat`
- `DELETE /seat/{roomId}/{seatId}`

### Reservation

- `POST /reservation`
- `PUT /reservation`
- `DELETE /reservation`
- `GET /reservation/me`

예약 생성/수정/취소/사용자별 조회에서 사용자 식별자는 요청 본문이 아니라 `ActorContextHolder`의 actor subject를 기준으로 처리합니다.

### Availability

- `GET /rooms/{roomId}/available-seats?start={instant}&end={instant}`
- `GET /rooms/{roomId}/seat-statuses?start={instant}&end={instant}`

`available-seats`는 예약 가능한 좌석만 반환하고, `seat-statuses`는 방에 존재하는 좌석별 `OCCUPIED`/`AVAILABLE` 상태를 반환합니다.

### Waitlist

- `POST /waitlist`
- `DELETE /waitlist/{waitlistId}`

## 테스트

테스트는 책임 경계별로 나뉩니다.

- application model test: `AvailableSeats`, `SeatReservationStatusClassifier`, `ReservationOccupancyPolicy`, `WaitlistRequests`
- use case/service test: 예약, 좌석 가용성, 좌석 상태, 대기열 유스케이스의 포트 wiring과 결과 변환 검증
- persistence adapter test: JPA criteria와 repository adapter 동작 검증
- controller test: 웹 요청이 command/query로 변환되는지 검증
- integration test: 예약 생성/수정/취소, 동시성, 대기열 흐름 검증

주요 실행 명령은 다음과 같습니다.

```bash
./gradlew :reservation:reservation-application:test
./gradlew :reservation:reservation-domain:test
```
