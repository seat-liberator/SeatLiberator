# Reservation Domain

`reservation:reservation-domain`은 reservation 도메인의 핵심 모델을 담는 domain 계층 모듈입니다.

이 모듈은 예약 도메인의 상태, 값 객체, 엔티티, 도메인 이벤트, 불변식 검증 규칙을 정의합니다.

## 역할

- 방, 좌석, 예약, 대기열 엔티티 정의
- 좌석 식별자와 시간 범위 값 객체 정의
- 좌석/예약/대기열 상태 enum 정의
- 예약 생성/취소/만료 도메인 이벤트 정의
- 예약 사용, 취소, 만료 등 도메인 상태 전이 규칙 보유
- 대기열 상태 불변식 검증
- 테스트 fixture 제공

## 계층 경계

이 모듈은 domain 계층에 해당합니다.

- 외부 요청, 저장소 adapter, 메시징 adapter의 흐름을 직접 조립하지 않습니다.
- 도메인 상태 전이와 불변식을 모델 내부에 둡니다.
- persistence annotation은 도메인 모델의 저장 형태를 표현하기 위해 사용합니다.
- 테스트 fixture는 도메인 모델을 사용하는 테스트에서 재사용할 수 있는 객체 생성을 제공합니다.

## 패키지 구조

### `reservation.domain`

도메인 값 객체 계약, simple 구현, embeddable 구현, 상태 enum을 담당합니다.

- value object contract: `SeatLocator`, `TimeRange`
- simple value object: `SimpleSeatLocator`, `SimpleTimeRange`, `SeatLocatorKey`
- embeddable value object: `EmbeddableSeatLocator`, `EmbeddableTimeRange`
- status enum: `SeatStatus`, `ReservationStatus`, `WaitlistStatus`, `WaitlistResolution`, `WaitlistBehavior`

`SeatLocator`와 `TimeRange`는 인터페이스 계약을 기준으로 simple 구현과 embeddable 구현을 나눕니다.

### `reservation.domain.persistence`

저장 가능한 도메인 엔티티와 상태 객체를 담당합니다.

- `Room`: 방 식별자와 생성 시각 보유
- `Seat`: 좌석 식별자와 활성/비활성 상태 전이 보유
- `Reservation`: 예약 대상, 시간 범위, 예약 상태 전이 보유
- `Waitlist`: 대기열 요청 대상, 처리 방식, 상태 전이 보유
- `WaitlistState`: 대기열 상태와 resolution, 처리 시각 보유

`Reservation`은 생성/취소/만료 시 도메인 이벤트를 등록합니다.
`Waitlist`의 상태 전이 불변식은 `WaitlistState`와 validator가 함께 보장합니다.

### `reservation.domain.event`

예약 도메인 이벤트를 담당합니다.

- `DomainEvent`
- `ReservationCreated`
- `ReservationCanceled`
- `ReservationExpired`

### `reservation.domain.validator`

대기열 상태 불변식 검증을 담당합니다.

- `WaitlistStateValidator`

### `src/testFixtures`

도메인 테스트 fixture를 제공합니다.

- `RoomFixture`, `SeatFixture`, `ReservationFixture`, `WaitlistFixture`
- `SeatLocatorFixture`, `TimeRangeFixture`
- `WaitlistFixtureBuilder`
- `TestSupport`

## 빌드 구성

주요 의존성은 다음과 같습니다.

- `:kernel:kernel-core`
- `java-test-fixtures`

## 테스트

모듈 테스트는 도메인 모델의 값 계약과 상태 전이를 중심으로 구성합니다.

- value object test: `SeatLocator`, `TimeRange` 계약과 simple/embeddable 구현 검증
- entity test: `Room`, `Seat`, `Reservation`, `Waitlist` 상태 전이 검증
- event test: 예약 생성/취소/만료 이벤트 등록 검증
- validator test: 대기열 상태 불변식 검증

실행 명령:

```bash
./gradlew :reservation:reservation-domain:test
```
