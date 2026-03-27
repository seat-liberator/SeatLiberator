package com.seatliberator.seatliberator.vacancy.infrastructure.web.controller;

import com.seatliberator.seatliberator.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.vacancy.infrastructure.web.request.VacancyAlertCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

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

}
