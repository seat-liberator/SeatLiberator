package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CreateBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateBookingCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingResult;
import com.seatliberator.seatliberator.reservation.application.occupancy.contract.SeatOccupancyCreator;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCreateAuthorizer;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateBookingService implements
        CreateBookingUseCase {

    private final ReservationCreateAuthorizer authorizer;
    private final ReservationCreator reservationCreator;
    private final SeatOccupancyCreator occupancyCreator;
    private final ActorContextHolder actorContextHolder;

    @Override
    public BookingResult create(CreateBookingCommand command) {
        var actor = actorContextHolder.getActor();
        var slotIds = command.seatTimeSlotIds();

        authorizer.validate(actor);
        var reservation = reservationCreator.create(command.userId());
        occupancyCreator.create(reservation.getId(), slotIds, command.occupancyDate());

        return BookingResult.from(reservation);
    }
}
