package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CancelBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CancelBookingCommand;
import com.seatliberator.seatliberator.reservation.application.occupancy.contract.SeatOccupancyReleaser;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCancelAuthorizer;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelBookingService implements CancelBookingUseCase {
    private final ReservationReader reader;

    private final ReservationCancelAuthorizer authorizer;
    private final ReservationOwnershipPolicy ownershipPolicy;
    private final SeatOccupancyReleaser occupancyReleaser;

    private final ApplicationEventPublisher eventPublisher;
    private final ActorContextHolder actorContextHolder;
    private final Clock clock;

    @Override
    public ReservationResult cancel(CancelBookingCommand command) {
        var actor = actorContextHolder.getActor();
        var reservationId = command.reservationId();

        authorizer.validate(actor);

        var reservation = reader.findById(reservationId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));

        ownershipPolicy.validate(reservation, actor);
        var releasedResult = occupancyReleaser.release(reservation.getId());

        var now = clock.instant();
        reservation.cancel(now);

        // SeatOccupancyReleased 이벤트 발행
        eventPublisher.publishEvent(releasedResult.toEvent());

        return ReservationResult.from(reservation);
    }
}
