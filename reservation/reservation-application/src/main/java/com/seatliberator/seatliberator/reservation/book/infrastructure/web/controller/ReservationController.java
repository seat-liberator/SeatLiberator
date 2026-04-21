package com.seatliberator.seatliberator.reservation.book.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CancelReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.UpdateReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CancelReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.UpdateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.infrastructure.web.request.ReservationUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {
    private final UpdateReservationUseCase updateReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;

    private final ActorContextHolder actorContextHolder;

    public ResponseEntity<?> update(
            @RequestBody ReservationUpdateRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new UpdateReservationCommand(
                userId,
                request.roomId(),
                request.seatId(),
                request.startAt(),
                request.endAt()
        );
        var result = updateReservationUseCase.update(command);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<?> delete() {
        var userId = actorContextHolder.getActor().subject();
        var command = new CancelReservationCommand(userId);
        var result = cancelReservationUseCase.cancel(command);
        return ResponseEntity.ok(result);
    }
}
