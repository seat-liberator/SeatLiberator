package com.seatliberator.seatliberator.reservation.book.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CancelReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.UpdateReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CancelReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.UpdateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.infrastructure.web.request.ReservationCreateRequest;
import com.seatliberator.seatliberator.reservation.book.infrastructure.web.request.ReservationUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final UpdateReservationUseCase updateReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;

    private final ActorContextHolder actorContextHolder;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ReservationCreateRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new CreateReservationCommand(
                userId,
                request.roomId(),
                request.seatId(),
                request.startAt(),
                request.endAt()
        );
        var result = createReservationUseCase.create(command);

        return ResponseEntity.ok(result);
    }

    @PutMapping
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
