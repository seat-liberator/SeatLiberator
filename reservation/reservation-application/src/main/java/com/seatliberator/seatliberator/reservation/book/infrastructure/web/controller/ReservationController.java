package com.seatliberator.seatliberator.reservation.book.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationManager;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationUpdateCommand;
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

    private final ReservationManager reservationManager;

    private final ActorContextHolder actorContextHolder;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ReservationCreateRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new ReservationCreateCommand(
                userId,
                request.roomId(),
                request.seatId(),
                request.startAt(),
                request.endAt()
        );
        var result = reservationManager.create(command);

        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<?> update(
            @RequestBody ReservationUpdateRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new ReservationUpdateCommand(
                userId,
                request.roomId(),
                request.seatId(),
                request.startAt(),
                request.endAt()
        );
        var result = reservationManager.update(command);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<?> delete() {
        var userId = actorContextHolder.getActor().subject();
        var result = reservationManager.cancel(userId);
        return ResponseEntity.ok(result);
    }

}
