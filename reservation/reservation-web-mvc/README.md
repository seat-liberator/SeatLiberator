# Reservation Web MVC

`reservation:reservation-web-mvc`는 reservation HTTP API를 제공하는 web adapter 모듈입니다.

이 모듈은 Spring Web MVC 기반 컨트롤러, 요청 DTO, 보안 권한 설정, 전역 예외 응답, OpenAPI 설정을 담당합니다.

## 역할

- 방/좌석 HTTP API 제공
- 예약 생성/수정/취소/조회/사용 HTTP API 제공
- 좌석 가용성/점유 상태 조회 HTTP API 제공
- 대기열 생성/취소 HTTP API 제공
- Actor context 기반 요청자 식별
- controller advice 기반 ProblemDetail 응답 변환
- reservation 권한 capability 설정
- reservation web application bootstrap 제공

## 계층 경계

이 모듈은 web adapter 계층에 해당합니다.

- HTTP 요청을 command/query로 변환해 inbound port를 호출합니다.
- HTTP 응답과 ProblemDetail 변환을 담당합니다.
- 보안 annotation과 capability 구성을 통해 endpoint 접근 조건을 표현합니다.
- 도메인 상태 전이와 저장소 접근을 직접 처리하지 않습니다.

## 패키지 구조

### `infrastructure.web.room`

방과 좌석 API를 담당합니다.

- controller: `RoomCommandController`, `RoomQueryController`, `SeatCommandController`, `SeatQueryController`
- request: `CreateRoomRequest`, `UpdateRoomRequest`, `SeatCreateRequest`, `SeatUpdateRequest`

주요 권한:

- `room.manage`
- `room.list`
- `room.read`
- `seat.list`
- `seat.read`

### `infrastructure.web.book`

예약 생성/수정/취소/조회 API를 담당합니다.

- controller: `CreateReservationController`, `ReservationController`, `ReservationQueryController`
- request: `ReservationCreateRequest`, `ReservationUpdateRequest`

예약 생성/수정/취소/조회에서 사용자 식별자는 `ActorContextHolder`의 actor subject를 사용합니다.

### `infrastructure.web.usage`

예약 사용 API를 담당합니다.

- controller: `ReservationUsageController`

예약 사용 요청은 path variable의 예약 ID와 `ActorContextHolder`의 actor를 기반으로 command를 생성합니다.

### `infrastructure.web.availability`

좌석 가용성/점유 상태 조회 API를 담당합니다.

- controller: `SeatAvailabilityController`

### `infrastructure.web.waitlist`

대기열 API를 담당합니다.

- controller: `WaitlistController`
- request: `CreateWaitlistRequest`

### `infrastructure.web.shared`

web adapter 공통 구성을 담당합니다.

- advice: `ReservationGlobalControllerAdvice`
- openapi: `ReservationProblemDetailOpenApiConfiguration`
- security: `ReservationRoleCapabilityConfiguration`

## Endpoint 구성

### Room and Seat

- `GET /rooms`
- `GET /rooms/{roomId}`
- `POST /rooms`
- `PUT /rooms/{roomId}`
- `DELETE /rooms/{roomId}`
- `GET /rooms/{roomId}/seats`
- `GET /rooms/{roomId}/seats/{seatId}`
- `POST /rooms/{roomId}/seats`
- `PUT /rooms/{roomId}/seats/{seatId}/id`
- `DELETE /rooms/{roomId}/seats/{seatId}`

### Reservation

- `POST /rooms/{roomId}/seats/{seatId}/reservations`
- `PUT /reservations`
- `DELETE /reservations`
- `GET /reservations/me`
- `POST /reservations/{reservationId}`

### Availability

- `GET /rooms/{roomId}/available-seats`
- `GET /rooms/{roomId}/seat-statuses`
- `GET /rooms/{roomId}/seats/{seatId}/occupy`

### Waitlist

- `POST /waitlist`
- `DELETE /waitlist/{waitlistId}`

## 빌드 구성

주요 의존성은 다음과 같습니다.

- `:reservation:reservation-persistence`
- `:reservation:reservation-application`
- `:reservation:reservation-domain`
- `:bootstrap:resource-application-starter`
- `testFixtures(project(":reservation:reservation-domain"))`

## 테스트

모듈 테스트는 controller와 web adapter 동작을 중심으로 구성합니다.

- MVC slice test: 요청 parameter/body/path variable을 command/query로 변환하는지 검증
- response test: 유스케이스 결과를 HTTP 응답으로 반환하는지 검증
- validation/error test: 잘못된 요청과 application 예외가 기대한 HTTP 상태로 변환되는지 검증
- security test: endpoint 권한 조건과 인증/인가 응답 검증
- OpenAPI test: ProblemDetail 문서화 설정 검증

실행 명령:

```bash
./gradlew :reservation:reservation-web-mvc:test
```
