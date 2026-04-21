package com.seatliberator.seatliberator.reservation.book.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.infrastructure.web.request.ReservationCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class CreateReservationController {
    private final CreateReservationUseCase createReservationUseCase;
    private final ActorContextHolder actorContextHolder;

    @PostMapping("/{roomId}/seats/{seatId}/reservations")
    public ResponseEntity<?> create(
            @PathVariable("roomId") String roomId,
            @PathVariable("seatId") String seatId,
            @RequestBody ReservationCreateRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new CreateReservationCommand(
                userId,
                roomId,
                seatId,
                request.startAt(),
                request.endAt()
        );
        var result = createReservationUseCase.create(command);

        return ResponseEntity.ok(result);
    }
}
