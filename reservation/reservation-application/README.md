# Reservation Application

`reservation:reservation-application`은 reservation 도메인의 application 계층 모듈입니다.

이 모듈은 유스케이스와 application port를 통해 예약 도메인 흐름을 조립합니다.

## 역할

- 방/좌석 관리 유스케이스 제공
- 예약 생성/수정/취소/조회 유스케이스 제공
- 예약 사용 처리 유스케이스 제공
- 좌석 가용성 및 점유 구간 조회 유스케이스 제공
- 대기열 요청 생성/취소와 빈자리 발생 시 대기열 승격 흐름 처리
- application 계층 공통 정책, 예외, notification relay 연계, seed 흐름 제공

## 계층 경계

이 모듈은 hexagonal architecture 기준의 application 계층에 해당합니다.

- `port/in`: 외부 어댑터가 호출하는 유스케이스 인터페이스와 command/query/result
- `port/out`: 저장소, 외부 시스템 등 구현 어댑터가 제공해야 하는 outbound port
- `service`: use case 구현체
- `contract`: application 내부 협력 객체와 정책 인터페이스
- `model`, `internal`, `handler`: application 흐름을 구성하는 내부 모델과 도메인 이벤트 처리

## 패키지 구조

### `application.room`

방과 좌석 관리 유스케이스를 담당합니다.

- inbound port: `CreateRoomUseCase`, `UpdateRoomUseCase`, `DeleteRoomUseCase`, `ListRoomUseCase`, `FindRoomUseCase`
- inbound port: `CreateSeatUseCase`, `UpdateSeatUseCase`, `MoveSeatUseCase`, `DeleteSeatUseCase`, `ListSeatUseCase`, `FindSeatUseCase`
- outbound port: `RoomReader`, `RoomStore`, `SeatReader`, `SeatStore`
- service: `RoomCommandService`, `RoomQueryService`, `SeatCommandService`, `SeatQueryService`
- internal: `SeatAssignmentService`

### `application.booking`

예약 생성/수정/취소/조회와 예약 정책을 담당합니다.

- inbound port: `CreateReservationUseCase`, `UpdateReservationUseCase`, `CancelReservationUseCase`, `FindMyReservationUseCase`
- outbound port: `ReservationReader`, `ReservationStore`
- criteria: `ReservationFilter`, `ReservationSeatOverlapCriteria`, `ReservationRoomOverlapCriteria`, `ReservationRangeOverlapCriteria`, `ReservationSeatLookupCriteria`
- service: `ReservationCommandService`, `ReservationQueryService`
- contract: `ReservationPolicyChecker`, `ReservationOwnershipPolicy`, `OccupancySeatLocatorFinder`, `OccupancySeatRangeFinder`
- model: `ReservationOccupancyPolicy`

`ReservationOwnershipPolicy`는 예약 소유자 또는 `BOOKING_MANAGE` capability 보유자를 예약 사용/접근 가능 주체로 판단합니다.

### `application.availability`

좌석 가용성, 좌석별 예약 상태, 점유 구간 조회를 담당합니다.

- inbound port: `FindAvailableSeatsUseCase`, `FindSeatStatusesUseCase`, `FindSeatOccupancyRangesUseCase`
- service: `SeatAvailabilityService`
- model: `AvailableSeats`, `SeatReservationStatus`, `SeatReservationStatusClassifier`

예약 점유 판단은 booking contract의 occupancy finder를 사용합니다.

### `application.usage`

예약 사용 처리를 담당합니다.

- inbound port: `UseReservationUseCase`
- command/result: `UseReservationCommand`, `UseReservationResult`
- service: `ReservationUsageService`

`ReservationUsageService`는 예약을 ID로 조회하고, `ReservationOwnershipPolicy`로 요청 actor의 접근 가능 여부를 판단한 뒤 예약을 사용 처리합니다.
상태 변경은 트랜잭션 안에서 수행됩니다.

### `application.waitlist`

대기열 요청과 승격 흐름을 담당합니다.

- inbound port: `CreateWaitlistUseCase`, `CancelWaitlistUseCase`
- outbound port: `WaitlistStore`
- service: `WaitlistService`
- internal: `WaitlistPromotion`, `WaitlistPromotionResult`
- handler: `SeatVacancyHandler`
- model: `WaitlistRequests`, `WaitlistProcessingResult`, `WaitlistNotification`

예약 취소/만료로 좌석이 비면 `SeatVacancyHandler`가 대기열 승격 흐름을 시작합니다.

### `application.shared`

application 계층 공통 구성을 제공합니다.

- `ReservationApplicationConfiguration`: namespace provider, clock, notifier bean 구성
- `ReservationCapability`: reservation application capability 정의
- `ReservationApplicationException`, `ReservationApplicationErrorCode`: application 예외와 에러 코드
- `PolicyDecision`, `PolicyReason`, `PolicyResult`: 내부 정책 결과 공통 계약
- `Notifier`: notification event relay 연계
- `ApplicationSeedRunner`, `RoomSeeder`: local seed 흐름

## 빌드 구성

주요 의존성은 다음과 같습니다.

- `:reservation:reservation-api`
- `:reservation:reservation-domain`
- `:notification:notification-api`
- `:bootstrap:application-starter`
- `testFixtures(project(":reservation:reservation-domain"))`
- `testFixtures(project(":identity:identity-core"))`

## 테스트

모듈 테스트는 application 계층 경계를 중심으로 구성합니다.

- use case/service test: inbound port 인터페이스를 기준으로 service 동작 검증
- contract test: 예약 정책, 소유권 정책, occupancy finder 검증
- model test: application 내부 모델과 classifier 검증
- criteria test: outbound port 조회 criteria의 값 구성 검증
- handler/internal test: 대기열 승격과 빈자리 처리 흐름 검증

실행 명령:

```bash
./gradlew :reservation:reservation-application:test
```
