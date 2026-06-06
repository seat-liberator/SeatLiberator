# Reservation Web MVC

`reservation:reservation-web-mvc`는 reservation HTTP API를 제공하는 web adapter 모듈입니다.

이 모듈은 Spring Web MVC 기반 컨트롤러, 요청 DTO, 보안 권한 설정, 전역 예외 응답, OpenAPI 설정, reservation 애플리케이션
bootstrap을 담당합니다. REST API는 컨트롤러가 직접 `/api/v1` prefix를 노출합니다.

## 역할

- 방/좌석/좌석 시간 슬롯 HTTP API 제공
- 예약 생성/취소/조회/사용 HTTP API 제공
- 좌석 예약 가능 슬롯 조회 HTTP API 제공
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

### `reservation.web.room`

방 조회/관리 API를 담당합니다.

- controller: `RoomCommandController`, `RoomQueryController`
- request: `CreateRoomRequest`, `UpdateRoomCodeRequest`, `UpdateRoomOperationPolicyRequest`

### `reservation.web.seat`

좌석과 좌석 시간 슬롯 조회/관리 API를 담당합니다.

- controller: `SeatCommandController`, `SeatQueryController`, `SeatTimeSlotCommandController`,
  `SeatTimeSlotQueryController`
- request: `SeatCreateRequest`, `SeatUpdateCodeRequest`, `SeatTimeSlotCreateRequest`, `SeatTimeSlotUpdateRequest`

### `reservation.web.booking`

예약 생성/취소/조회와 예약 가능 슬롯 조회 API를 담당합니다.

- controller: `BookingController`, `AvailabilityQueryController`
- request: `CreateBookingRequest`, `CancelBookingRequest`

예약 생성에서는 `ActorContextHolder`의 actor subject를 예약 사용자 식별자로 사용합니다.

### `reservation.web.reservation`

예약 목록/상세 조회와 예약 사용 API를 담당합니다.

- controller: `ReservationQueryController`, `UseReservationController`

### `reservation.web.waitlist`

대기열 생성/취소 API를 담당합니다.

- controller: `WaitlistController`
- request: `CreateWaitlistRequest`

대기열 생성에서는 `ActorContextHolder`의 actor subject를 요청자 식별자로 사용합니다.

### `reservation.web.shared`

web adapter 공통 구성을 담당합니다.

- advice: `ReservationGlobalControllerAdvice`
- openapi: `ReservationProblemDetailOpenApiConfiguration`
- security: `ReservationRoleCapabilityConfiguration`

## Endpoint 구성

### Rooms

- `GET /api/v1/rooms`
- `GET /api/v1/rooms/{roomId}`
- `POST /api/v1/rooms`
- `PUT /api/v1/rooms/{roomId}`
- `PUT /api/v1/rooms/{roomId}/policy`
- `DELETE /api/v1/rooms/{roomId}`

### Seats

- `GET /api/v1/rooms/{roomId}/seats`
- `GET /api/v1/rooms/{roomId}/seats/{seatId}`
- `POST /api/v1/rooms/{roomId}/seats`
- `PUT /api/v1/rooms/{roomId}/seats/{seatId}/id`
- `DELETE /api/v1/rooms/{roomId}/seats/{seatId}`

### Seat Time Slots

- `GET /api/v1/rooms/{roomId}/seats/{seatId}/slots`
- `GET /api/v1/rooms/{roomId}/seats/{seatId}/slots/{slotId}`
- `POST /api/v1/rooms/{roomId}/seats/{seatId}/slots`
- `PUT /api/v1/rooms/{roomId}/seats/{seatId}/slots/{slotId}`
- `DELETE /api/v1/rooms/{roomId}/seats/{seatId}/slots/{slotId}`

### Booking

- `GET /api/v1/booking/{reservationId}`
- `POST /api/v1/booking/booking`
- `DELETE /api/v1/booking/booking`
- `GET /api/v1/booking/seats/{seatId}/available-slots?start={yyyy-MM-dd}&end={yyyy-MM-dd}`

### Reservations

- `GET /api/v1/reservations?userId={userId}&status={status}`
- `GET /api/v1/reservations?userId={userId}&status={status}&start={isoInstant}&end={isoInstant}`
- `GET /api/v1/reservations/{reservationId}`
- `POST /api/v1/reservations/{reservationId}`

### Waitlist

- `POST /api/v1/waitlist`
- `DELETE /api/v1/waitlist/{waitlistId}`

## 권한 구성

`ReservationRoleCapabilityConfiguration`은 reservation capability를 기본 역할에 매핑합니다.

- `GUEST`: `room.list`, `seat.list`
- `USER`: `room.read`, `seat.read`, `booking.create`, `owned.booking.update`, `owned.booking.cancel`
- `MAINTAINER`: `room.manage`, `seat.manage`, `booking.manage`
- `ADMIN`: 별도 reservation capability 없음

## 빌드 구성

주요 의존성은 다음과 같습니다.

- `:reservation:reservation-persistence`
- `:reservation:reservation-application`
- `:reservation:reservation-domain`
- `:identity:identity-security-starter`
- `:kernel:kernel-test` (test)
- `testFixtures(project(":reservation:reservation-domain"))` (test)

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
