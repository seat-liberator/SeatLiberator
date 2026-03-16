package com.seatliberator.seatliberator.reservation.infrastructure.web.controller;


import com.seatliberator.seatliberator.reservation.application.port.in.SeatManager;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatUpdateCommand;
import com.seatliberator.seatliberator.reservation.infrastructure.web.request.SeatCreateRequest;
import com.seatliberator.seatliberator.reservation.infrastructure.web.request.SeatUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/seat")
public class SeatController {

    private final SeatManager seatManager;

    @PostMapping
    public Map<String, Boolean> create(
            @RequestBody SeatCreateRequest request
            ){
        boolean result = seatManager.create(
                new SeatCreateCommand(
                        request.roomId(),
                        request.seatId()
                )
        );

        return Map.of("success", result);
    }

    @PutMapping
    public Map<String, Boolean> update(
            @RequestBody SeatUpdateRequest request
            ){
        boolean result = seatManager.update(
                new SeatUpdateCommand(
                        request.roomId(),
                        request.seatId()
                )
        );

        return Map.of("success", result);
    }

    @DeleteMapping("/{roomId}/{seatId}")
    public Map<String, Boolean> delete(
            @PathVariable String roomId,
            @PathVariable String seatId
    ){
        boolean result = seatManager.delete(roomId, seatId);
        return Map.of("success", result);
    }
}
