# Reservation Persistence

`reservation:reservation-persistence`는 reservation 저장소 adapter를 담는 persistence 계층 모듈입니다.

이 모듈은 JPA 기반 repository와 adapter를 통해 reservation application의 outbound port를 구현합니다.

## 역할

- 방/좌석 저장소 adapter 제공
- 예약 저장소 adapter 제공
- 대기열 저장소 adapter 제공
- 조회 조건을 JPA specification으로 변환
- DB 기반 저장/조회/동시성/대기열 흐름 검증

## 계층 경계

이 모듈은 persistence adapter 계층에 해당합니다.

- application outbound port를 구현합니다.
- JPA repository와 specification을 사용해 저장소 접근을 처리합니다.
- 유스케이스 정책이나 도메인 상태 전이 규칙을 직접 결정하지 않습니다.
- HTTP 요청/응답을 직접 다루지 않습니다.

## 패키지 구조

### `persistence.room.jpa`

방과 좌석 저장소 adapter를 담당합니다.

- adapter: `JpaRoomPersistenceAdapter`, `JpaSeatPersistenceAdapter`
- repository: `RoomRepository`, `SeatRepository`

`JpaRoomPersistenceAdapter`는 `RoomReader`, `RoomStore`를 구현합니다.
`JpaSeatPersistenceAdapter`는 `SeatReader`, `SeatStore`를 구현합니다.

### `persistence.book.jpa`

예약 저장소 adapter를 담당합니다.

- adapter: `JpaReservationPersistenceAdapter`
- repository: `ReservationRepository`

`JpaReservationPersistenceAdapter`는 `ReservationReader`, `ReservationStore`를 구현합니다.
예약 조회 criteria는 JPA specification으로 변환해 처리합니다.

### `persistence.waitlist.jpa`

대기열 저장소 adapter를 담당합니다.

- adapter: `JpaWaitlistStore`
- repository: `WaitlistRepository`

`JpaWaitlistStore`는 `WaitlistStore`를 구현합니다.

### `persistence.shared.jpa.specification`

JPA specification predicate 생성을 담당합니다.

- `CommonPredicates`
- `SeatLocatorPredicates`
- `TimeRangePredicates`

## 빌드 구성

주요 의존성은 다음과 같습니다.

- `:reservation:reservation-application`
- `:reservation:reservation-domain`
- `:notification:notification-api`
- `testFixtures(project(":reservation:reservation-domain"))`

## 테스트

모듈 테스트는 저장소 adapter와 DB 연동 흐름을 중심으로 구성합니다.

- adapter test: JPA adapter가 outbound port 계약에 맞게 저장/조회하는지 검증
- criteria/specification test: 예약 조회 조건이 기대한 DB 조회로 동작하는지 검증
- integration test: 예약 생성/수정/취소, 대기열 요청, 동시성 흐름 검증

실행 명령:

```bash
./gradlew :reservation:reservation-persistence:test
```
