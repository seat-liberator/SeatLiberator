package com.seatliberator.seatliberator.reservation.book.domain;

import com.seatliberator.seatliberator.reservation.book.domain.event.ReservationCanceled;
import com.seatliberator.seatliberator.reservation.book.domain.event.ReservationCreated;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.seatliberator.seatliberator.reservation.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Reservation")
public class ReservationTest {

    @Nested
    @DisplayName("Transition")
    class Transition {
        @Nested
        @DisplayName("from RESERVED")
        class TransitionFromReserved {
            @Test
            @DisplayName("예약은 생성되면 RESERVED 상태다")
            void initial_reservation_status() {
                var r = createReservation();

                assertThat(r.getStatus()).isEqualTo(ReservationStatus.RESERVED);
                assertThat(r.isReserved()).isTrue();
            }

            @Test
            @DisplayName("예약 상태에서 사용 처리할 수 있다")
            void mark_used_when_reservation_is_used_within_valid_time() {
                var reservation = createReservation();
                var usedAt = reservation.getRange().startAt().plusSeconds(1);

                reservation.use(usedAt);

                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.USED);
                assertThat(reservation.isUsed()).isTrue();
            }

            @Test
            @DisplayName("예약 상태에서 취소 처리할 수 있다")
            void can_cancel_when_reserved() {
                var r = createReservation();
                var canceledAt = fixedClock.instant();

                r.cancel(canceledAt);

                assertThat(r.getStatus()).isEqualTo(ReservationStatus.CANCELED);
                assertThat(r.isCanceled()).isTrue();
            }
        }

        @Nested
        @DisplayName("from USED")
        class TransitionFromUsed {
            @Test
            @DisplayName("예약 시간 이전에 사용 처리할 수 없다")
            void can_not_used_before_reservation_start_at() {
                var r = createReservation();
                var usedAt = r.getRange().startAt().minusSeconds(1);

                assertThatThrownBy(() -> r.use(usedAt))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("사용 가능한 시간이 아닙니다.");

                assertThat(r.getStatus()).isEqualTo(ReservationStatus.RESERVED);
                assertThat(r.isReserved()).isTrue();
            }

            @Test
            @DisplayName("예약 시간 동안 취소할 수 있다")
            void can_cancel_while_used() {
                var usedAt = fixedClock.instant();

                var r1 = createReservation();
                var canceledAtStartEdge = r1.getRange().startAt();
                r1.use(usedAt);
                r1.cancel(canceledAtStartEdge);
                assertThat(r1.getStatus()).isEqualTo(ReservationStatus.CANCELED);
                assertThat(r1.isCanceled()).isTrue();

                var r2 = createReservation();
                var canceledAtEndEdge = r2.getRange().endAt().minusNanos(1);
                r2.use(usedAt);
                r2.cancel(canceledAtEndEdge);
                assertThat(r2.getStatus()).isEqualTo(ReservationStatus.CANCELED);
                assertThat(r2.isCanceled()).isTrue();
            }

            @Test
            @DisplayName("이미 사용 처리된 예약은 다시 사용 처리할 수 없다.")
            void throw_exception_when_marking_already_used_reservation() {
                var reservation = createReservation();
                var usedAt = fixedClock.instant();

                reservation.use(usedAt);

                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.USED);

                assertThatThrownBy(() -> reservation.use(usedAt))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("이미 사용된 예약입니다.");

                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.USED);
                assertThat(reservation.isUsed()).isTrue();
            }

            @Test
            @DisplayName("종료 시간이 지난 예약은 사용 처리 시 만료 예외를 던진다")
            void change_status_to_expired_when_marking_reservation_after_end_time() {
                var reservation = createReservation();
                var usedAt = reservation.getRange().endAt().plusSeconds(30);

                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);

                assertThatThrownBy(() -> reservation.use(usedAt))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("이미 만료된 예약입니다.");

                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
                assertThat(reservation.isExpired()).isTrue();
            }
        }

        @Nested
        @DisplayName("from CANCELED")
        class TransitionFromCanceled {
            @Test
            @DisplayName("취소된 예약은 다시 사용 처리할 수 없다")
            void can_not_use_when_already_canceled() {
                var r = createReservation();
                var canceledAt = fixedClock.instant();

                r.cancel(canceledAt);

                var usedAt = canceledAt.plusSeconds(1);

                assertThatThrownBy(() -> r.use(usedAt))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("이미 취소된 예약입니다.");

                assertThat(r.getStatus()).isEqualTo(ReservationStatus.CANCELED);
                assertThat(r.isCanceled()).isTrue();
            }

            @Test
            @DisplayName("사용하지 않고 종료 시간이 지난 예약은 취소 처리 시 만료 예외를 던진다")
            void throw_exception_when_try_cancel_already_expired() {
                var r = createReservation();
                var canceledAt = r.getRange().endAt().plusSeconds(3);

                assertThat(r.getStatus()).isEqualTo(ReservationStatus.RESERVED);

                assertThatThrownBy(() -> r.cancel(canceledAt))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("이미 만료된 예약입니다.");

                assertThat(r.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
                assertThat(r.isExpired()).isTrue();
            }
        }

        @Nested
        @DisplayName("from EXPIRED")
        class TransitionFromExpired {
            @Test
            @DisplayName("이미 만료된 예약은 사용 처리할 수 없다")
            void can_not_use_already_expired() {
                var r = createReservation(ReservationStatus.EXPIRED);
                var usedAt = r.getRange().startAt();

                assertThatThrownBy(() -> r.use(usedAt))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("이미 만료된 예약입니다.");
                assertThat(r.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
                assertThat(r.isExpired()).isTrue();
            }

            @Test
            @DisplayName("이미 만료된 예약은 취소 처리할 수 없다")
            void can_not_cancel_already_expired() {
                var r = createReservation(ReservationStatus.EXPIRED);
                var canceledAt = r.getRange().startAt();

                assertThatThrownBy(() -> r.cancel(canceledAt))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("이미 만료된 예약입니다.");
                assertThat(r.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
                assertThat(r.isExpired()).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("Event")
    class Event {
        @Test
        @DisplayName("예약 생성 시 ReservationCreated 이벤트도 만들어진다")
        void create_reservation_event() {
            var r = createReservation();

            assertThat(r.domainEvents())
                    .singleElement()
                    .isInstanceOfSatisfying(ReservationCreated.class, e -> {
                        assertThat(e.locator().roomId()).isEqualTo(INITIAL_ROOM_ID);
                        assertThat(e.locator().seatId()).isEqualTo(INITIAL_SEAT_ID);
                        assertThat(e.range().startAt()).isEqualTo(fixedClock.instant());
                        assertThat(e.range().endAt()).isEqualTo(fixedClock.instant().plus(INITIAL_DURATION));
                    });
        }

        @Test
        @DisplayName("예약 취소 시 ReservationCanceled 이벤트도 만들어진다")
        void create_canceled_event() {
            var r = createReservation();
            var canceledAt = fixedClock.instant();
            r.cancel(canceledAt);

            var events = new ArrayList<>(r.domainEvents());

            assertThat(events).hasSize(2);

            assertThat(events.get(0))
                    .isInstanceOfSatisfying(ReservationCreated.class, e -> {
                        assertThat(e.locator()).isEqualTo(r.getLocator());
                        assertThat(e.range()).isEqualTo(r.getRange());
                    });

            assertThat(events.get(1))
                    .isInstanceOfSatisfying(ReservationCanceled.class, e -> {
                        assertThat(e.locator()).isEqualTo(r.getLocator());
                        assertThat(e.range()).isEqualTo(r.getRange());
                        assertThat(e.canceledAt()).isEqualTo(canceledAt);
                    });
        }

        @Test
        @DisplayName("예약 취소에 실패하면 ReservationCanceled 이벤트는 생성되지 않는다")
        void does_not_create_canceled_event_when_cancel_fail() {
            var r = createReservation();
            var afterEndAt = r.getRange().endAt();

            assertThatThrownBy(() -> r.cancel(afterEndAt))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 만료된 예약입니다.");

            var events = new ArrayList<>(r.domainEvents());

            assertThat(events)
                    .singleElement()
                    .isInstanceOf(ReservationCreated.class);
        }
    }
}
