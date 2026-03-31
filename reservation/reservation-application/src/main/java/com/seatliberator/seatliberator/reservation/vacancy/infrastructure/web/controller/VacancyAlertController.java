package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.reservation.vacancy.infrastructure.web.request.VacancyAlertCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancy-alert")
public class VacancyAlertController {

    private final VacancyAlertRequester requester;

    private final ActorContextHolder actorContextHolder;

    // 알람 신청
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody VacancyAlertCreateRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        VacancyAlertRequestCommand command = new VacancyAlertRequestCommand(
                userId,
                request.roomId(),
                request.seatId(),
                request.startAt(),
                request.endAt()
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

        VacancyAlertCancelCommand command = new VacancyAlertCancelCommand(userId, alertId);

        requester.cancelVacancyAlert(command);

        return ResponseEntity.noContent().build();
    }
}
