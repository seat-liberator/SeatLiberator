package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindBookingQuery;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.BookingDetailReader;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationStateResult;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.application.DefaultTestSupport.ACTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindBookingUseCase 테스트")
public class FindBookingUseCaseTest {
    private static final UUID RESERVATION_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OCCUPANCY_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID SLOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Mock
    BookingDetailReader bookingDetailReader;
    @Mock
    ReservationOwnershipPolicy ownershipPolicy;
    @Mock
    ActorContextHolder actorContextHolder;

    FindBookingUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new FindBookingService(bookingDetailReader, ownershipPolicy, actorContextHolder);
    }

    @Test
    @DisplayName("예약 ID 조회 시 현재 actor의 소유권을 검증하고 booking 상세 projection을 조회한다")
    void should_find_booking_detail_by_reservation_id() {
        var query = FindBookingQuery.of(RESERVATION_ID);
        var detail = bookingDetailResult();

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(bookingDetailReader.findByReservationId(query.reservationId()))
                .thenReturn(Optional.of(detail));

        var actual = useCase.find(query);

        assertThat(actual).isEqualTo(detail);
        var inOrder = inOrder(actorContextHolder, ownershipPolicy, bookingDetailReader);
        inOrder.verify(actorContextHolder).getActor();
        inOrder.verify(ownershipPolicy).validate(query.reservationId(), ACTOR);
        inOrder.verify(bookingDetailReader).findByReservationId(query.reservationId());
        verifyNoMoreInteractions(actorContextHolder, ownershipPolicy, bookingDetailReader);
    }

    @Test
    @DisplayName("소유권 검증 후 예약 ID에 해당하는 booking 상세가 없으면 예약 없음 예외를 던진다")
    void should_throw_when_booking_detail_not_found() {
        var query = FindBookingQuery.of(RESERVATION_ID);

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(bookingDetailReader.findByReservationId(query.reservationId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.find(query))
                .isInstanceOfSatisfying(ReservationApplicationException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));

        var inOrder = inOrder(actorContextHolder, ownershipPolicy, bookingDetailReader);
        inOrder.verify(actorContextHolder).getActor();
        inOrder.verify(ownershipPolicy).validate(query.reservationId(), ACTOR);
        inOrder.verify(bookingDetailReader).findByReservationId(query.reservationId());
        verifyNoMoreInteractions(actorContextHolder, ownershipPolicy, bookingDetailReader);
    }

    @Test
    @DisplayName("현재 actor가 예약 소유자가 아니면 booking 상세를 조회하지 않는다")
    void should_not_find_booking_detail_when_actor_is_not_reservation_owner() {
        var query = FindBookingQuery.of(RESERVATION_ID);
        var exception = new ReservationApplicationPolicyException(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        doThrow(exception).when(ownershipPolicy).validate(query.reservationId(), ACTOR);

        assertThatThrownBy(() -> useCase.find(query))
                .isSameAs(exception);

        verify(actorContextHolder).getActor();
        verify(ownershipPolicy).validate(query.reservationId(), ACTOR);
        verifyNoInteractions(bookingDetailReader);
        verifyNoMoreInteractions(actorContextHolder, ownershipPolicy);
    }

    private BookingDetailResult bookingDetailResult() {
        var reservedAt = Instant.parse("2026-04-14T09:00:00Z");

        return new BookingDetailResult(
                RESERVATION_ID,
                "user-1",
                new ReservationStateResult(
                        ReservationStatus.RESERVED,
                        reservedAt,
                        null,
                        null,
                        null
                ),
                List.of(new BookingDetailResult.BookingSlotResult(
                        OCCUPANCY_ID,
                        SLOT_ID,
                        LocalDate.parse("2026-04-14"),
                        "study-room-1",
                        "seat-a",
                        LocalTime.of(9, 0),
                        Duration.ofHours(2),
                        SeatTimeSlotStatus.ACTIVE
                ))
        );
    }
}
