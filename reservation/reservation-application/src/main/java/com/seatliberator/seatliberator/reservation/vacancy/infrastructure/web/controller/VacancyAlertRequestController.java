package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCreateCommand;
import com.seatliberator.seatliberator.reservation.vacancy.infrastructure.web.request.VacancyAlertRequestCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancy-alert")
public class VacancyAlertRequestController {

    private final VacancyAlertRequester requester;

    private final ActorContextHolder actorContextHolder;

    // 알람 신청
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody VacancyAlertRequestCreateRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new VacancyAlertRequestCreateCommand(
                userId,
                request.roomId(),
                request.seatId(),
                request.startAt(),
                request.endAt(),
                request.behavior()
        );

        requester.request(command);

        return ResponseEntity.ok().build();
    }

    // 알람 취소
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID alertId
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new VacancyAlertRequestCancelCommand(userId, alertId);

        requester.cancel(command);

        return ResponseEntity.noContent().build();
    }
}
