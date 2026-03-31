# 예약 취소와 빈자리 알림 생성 흐름

## 목적

이 문서는 이슈 [#26](https://github.com/seat-liberator/SeatLiberator/issues/26) 과 상위
이슈 [#23](https://github.com/seat-liberator/SeatLiberator/issues/23) 에서 요구한 문서화 항목 중, 예약 취소와 빈자리 알림 생성 연계 흐름을 정리합니다.

핵심 목표는 다음 세 가지입니다.

- 예약 취소 시 빈자리 알림 대상자를 어떻게 찾는지 설명한다.
- 유효한 신청자에게만 알림 생성 흐름이 연결되는 조건을 명시한다.
- 예약 취소와 알림 생성을 직접 결합하지 않는 최소 연계 구조를 설명한다.

## 관련 도메인

| 구성요소                                    | 역할                                                                                |
|-----------------------------------------|-----------------------------------------------------------------------------------|
| `Reservation`                           | 좌석 예약 aggregate. 취소 시 `CANCELED` 로 전이되고 `ReservationCanceled` 이벤트를 발행한다.          |
| `ReservationCanceled`                   | 취소된 예약의 좌석(`SeatLocator`)과 시간 범위(`TimeRange`), 취소 시각을 담는 도메인 이벤트                  |
| `VacancyAlertRequest`                   | 특정 좌석/시간대에 대한 빈자리 알림 신청. 상태는 `ACTIVE`, `CANCELLED`, `EXPIRED`, `FULFILLED` 를 가진다. |
| `ReservationCanceledHandler`            | 예약 취소 이벤트를 받아 빈자리 알림 대상 조회와 notification 생성 이벤트 발행을 담당한다.                         |
| `NotificationCreateRequestEventPayload` | notification 모듈로 전달하는 생성 요청 payload                                               |

## 시나리오

### 1. 빈자리 알림 신청

사용자는 특정 좌석과 시간대에 대해 빈자리 알림을 신청할 수 있습니다.

- 식별 기준은 `(userId, roomId, seatId, targetStartAt, targetEndAt)` 입니다.
- 같은 조합의 `ACTIVE` 요청은 중복 생성할 수 없습니다.
- 생성 직후 상태는 `ACTIVE` 입니다.

### 2. 예약 취소

예약이 취소되면 `Reservation.cancel(canceledAt)` 이 호출됩니다.

- 예약 상태는 `CANCELED` 로 전이됩니다.
- 같은 트랜잭션 안에서 `ReservationCanceled(locator, range, canceledAt)` 이벤트가 등록됩니다.

### 3. 빈자리 알림 대상 조회

`ReservationCanceledHandler` 는 이벤트의 좌석과 시간 범위를 기준으로 `ACTIVE` 요청만 조회합니다.

조회 조건은 다음과 같습니다.

- 같은 `roomId`, `seatId`
- 상태가 `ACTIVE`
- 시간 구간이 겹침

시간 구간은 반개구간 `[start, end)` 기준으로 판단합니다.

```text
request.start < reservation.end
AND request.end > reservation.start
```

즉, 아래 요청은 대상에서 제외됩니다.

- 이미 취소되었거나 만료되었거나 이미 처리 완료된 요청
- 다른 좌석에 대한 요청
- 시간이 맞닿기만 하고 실제로 겹치지 않는 요청

### 4. 알림 생성 연계

조회된 각 요청에 대해 handler는 두 가지를 수행합니다.

1. notification 모듈로 `NOTIFICATION_CREATE_REQUEST` 이벤트를 발행합니다.
2. 해당 `VacancyAlertRequest` 상태를 `FULFILLED` 로 전이합니다.

이때 notification 본문에는 사용자에게 다시 확인할 좌석/시간대 정보가 포함됩니다.

이 구조의 의도는 다음과 같습니다.

- 예약 도메인은 "취소됨"이라는 사실만 발행한다.
- 빈자리 알림 대상 판별은 vacancy 쪽 후속 처리에서 맡는다.
- 실제 알림 저장/전달 채널 구현은 notification 모듈 이후 단계로 분리한다.

## 검증 기준

현재 구현은 통합 테스트로 아래 조건을 검증합니다.

- 예약 취소 시 같은 좌석/시간대의 활성 요청에 대해서만 notification 생성 이벤트가 발행된다.
- 미리 취소된 요청은 대상이 아니다.
- 다른 좌석 요청은 대상이 아니다.
- 실제 대상 요청만 `FULFILLED` 로 전이된다.

관련 테스트:

- `src/test/java/com/seatliberator/seatliberator/reservation/integration/ReservationCancelVacancyAlertFlowTest.java`

## 남겨둔 범위

이 문서는 현재 구현 범위를 기준으로 합니다. 다음 항목은 의도적으로 포함하지 않습니다.

- 외부 메시지 브로커 연동
- outbox / inbox / idempotency
- 이메일, 푸시, SSE, WebSocket 같은 전달 채널 구현
- 알림 문구/템플릿 고도화
