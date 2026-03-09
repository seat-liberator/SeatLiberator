package com.seatliberator.seatliberator.reservation.infrastructure.web.controller;

import com.seatliberator.seatliberator.reservation.application.port.in.ReservationManager;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationUpdateCommand;
import com.seatliberator.seatliberator.reservation.infrastructure.web.request.ReservationCreateRequest;
import com.seatliberator.seatliberator.reservation.infrastructure.web.request.ReservationUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationManager reservationManager;

    @PostMapping
    public Map<String, Boolean> create(
            @RequestBody ReservationCreateRequest request
    ) {
        boolean result = reservationManager.create(
                new ReservationCreateCommand(
                        request.userId(),
                        request.roomId(),
                        request.seatId(),
                        request.startTime(),
                        request.endTime()
                )
        );

        return Map.of("success", result);
    }

    @PutMapping
    public Map<String, Boolean> update(
            @RequestBody ReservationUpdateRequest request
    ) {
        boolean result = reservationManager.update(
                new ReservationUpdateCommand(
                        request.userId(),
                        request.roomId(),
                        request.seatId(),
                        request.startTime(),
                        request.endTime()
                )
        );

        return Map.of("success", result);
    }



}
