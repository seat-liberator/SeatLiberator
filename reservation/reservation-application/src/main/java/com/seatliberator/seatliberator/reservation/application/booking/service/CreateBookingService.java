package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CreateBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateBookingCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingResult;
import com.seatliberator.seatliberator.reservation.application.occupancy.contract.SeatOccupancyCreator;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateBookingService implements
        CreateBookingUseCase {
    private final SeatTimeSlotReader slotReader;

    private final ReservationCreator reservationCreator;
    private final SeatOccupancyCreator occupancyCreator;
    private final ActorContextHolder actorContextHolder;

    @Override
    public BookingResult create(CreateBookingCommand command) {
        var actor = actorContextHolder.getActor();

        var reservation = reservationCreator.createAuthorized(command.userId(), actor);
        var slots = slotReader.findByIds(command.seatTimeSlotIds());
        occupancyCreator.create(reservation, slots, command.occupancyDate());

        return BookingResult.from(reservation, slots);
    }
}
