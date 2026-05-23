package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CreateBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateBookingCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingResult;
import com.seatliberator.seatliberator.reservation.application.occupancy.contract.SeatOccupancyAllocator;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCreateAuthorizer;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateBookingService implements CreateBookingUseCase {

    private final ReservationCreateAuthorizer authorizer;
    private final ReservationCreator reservationCreator;
    private final SeatOccupancyAllocator occupancyAllocator;

    private final ApplicationEventPublisher eventPublisher;
    private final ActorContextHolder actorContextHolder;

    @Override
    public BookingResult create(CreateBookingCommand command) {
        var actor = actorContextHolder.getActor();
        var slotIds = command.seatTimeSlotIds();

        authorizer.validate(actor);
        var reservation = reservationCreator.create(command.userId());
        var allocatedResult = occupancyAllocator.allocate(reservation.getId(), slotIds, command.occupancyDate());

        // SeatOccupancyAllocated 이벤트 발행
        eventPublisher.publishEvent(allocatedResult.toEvent());

        return BookingResult.from(reservation);
    }
}
