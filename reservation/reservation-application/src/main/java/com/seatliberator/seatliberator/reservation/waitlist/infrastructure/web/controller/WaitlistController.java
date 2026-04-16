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

    // 대기열 등록
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

    // 대기열 취소
    @DeleteMapping("/{waitlistId}")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID waitlistId
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new CancelWaitlistCommand(userId, waitlistId);

        cancelWaitlistUseCase.cancel(command);

        return ResponseEntity.noContent().build();
    }
}
