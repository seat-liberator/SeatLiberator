package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CreateBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingResult;
import com.seatliberator.seatliberator.reservation.application.occupancy.contract.SeatOccupancyCreator;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCreateAuthorizer;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.application.booking.BookingTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateBookingUseCase 테스트")
public class CreateBookingUseCaseTest {

    @Mock
    ReservationCreateAuthorizer authorizer;

    @Mock
    ReservationCreator reservationCreator;

    @Mock
    SeatOccupancyCreator occupancyCreator;

    @Mock
    ActorContextHolder actorContextHolder;

    CreateBookingUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new CreateBookingService(authorizer, reservationCreator, occupancyCreator, actorContextHolder);
    }

    @Test
    @DisplayName("예약 생성 시 현재 actor로 권한 예약을 만들고 슬롯 점유 생성을 위임한다")
    void create_booking_delegates_to_creators() {
        var command = createBookingCommand();
        var reservation = reservation();

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(reservationCreator.create(command.userId())).thenReturn(reservation);

        useCase.create(command);

        verify(actorContextHolder).getActor();
        verify(reservationCreator).create(command.userId());
        verify(occupancyCreator).create(reservation.getId(), command.seatTimeSlotIds(), command.occupancyDate());
        verifyNoMoreInteractions(actorContextHolder, reservationCreator, occupancyCreator);
    }

    @Test
    @DisplayName("생성된 예약과 슬롯들을 BookingResult로 변환해 반환한다")
    void create_maps_created_reservation_and_slots_to_booking_result() {
        var command = createBookingCommand();
        var reservation = reservation();

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(reservationCreator.create(command.userId())).thenReturn(reservation);

        var result = useCase.create(command);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(BookingResult.from(reservation));
        assertThat(result.reservation())
                .usingRecursiveComparison()
                .isEqualTo(ReservationResult.from(reservation));
    }
}
