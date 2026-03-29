package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.web.controller;

import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.reservation.vacancy.infrastructure.web.request.VacancyAlertCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancy-alert")
public class VacancyAlertController {

    private final VacancyAlertRequester requester;

    // 알람 신청
    @PostMapping
    public ResponseEntity<Void> create(
            //@RequestHeader("userId") String userId,
            @RequestBody VacancyAlertCreateRequest request
    ) {

        VacancyAlertRequestCommand command = new VacancyAlertRequestCommand(
                request.userId(),
                request.roomId(),
                request.seatId(),
                request.targetStartTime(),
                request.targetEndTime(),
                Instant.now()
        );

        requester.request(command);

        return ResponseEntity.ok().build();
    }

    // 알람 취소
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> cancel(
            @RequestHeader("userId") String userId,
            @PathVariable UUID alertId
    ) {

        VacancyAlertCancelCommand command = new VacancyAlertCancelCommand(userId, alertId);

        requester.cancelVacancyAlert(command);

        return ResponseEntity.noContent().build();
    }
}
