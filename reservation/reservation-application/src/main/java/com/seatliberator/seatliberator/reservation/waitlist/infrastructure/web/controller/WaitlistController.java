package com.seatliberator.seatliberator.reservation.waitlist.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.CancelWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.CreateWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CancelWaitlistCommand;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CreateWaitlistCommand;
import com.seatliberator.seatliberator.reservation.waitlist.infrastructure.web.request.CreateWaitlistRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/waitlist")
public class WaitlistController {

    private final CreateWaitlistUseCase createWaitlistUseCase;
    private final CancelWaitlistUseCase cancelWaitlistUseCase;

    private final ActorContextHolder actorContextHolder;

    // 알람 신청
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody CreateWaitlistRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new CreateWaitlistCommand(
                userId,
                request.roomId(),
                request.seatId(),
                request.startAt(),
                request.endAt(),
                request.behavior()
        );

        createWaitlistUseCase.create(command);

        return ResponseEntity.ok().build();
    }

    // 알람 취소
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID alertId
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new CancelWaitlistCommand(userId, alertId);

        cancelWaitlistUseCase.cancel(command);

        return ResponseEntity.noContent().build();
    }
}
