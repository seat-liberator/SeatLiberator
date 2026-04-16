package com.seatliberator.seatliberator.reservation.seat.infrastructure.web.controller;


import com.seatliberator.seatliberator.reservation.seat.application.port.in.CreateSeatUseCase;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.DeleteSeatUseCase;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.UpdateSeatUseCase;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.DeleteSeatCommand;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.UpdateSeatCommand;
import com.seatliberator.seatliberator.reservation.seat.infrastructure.web.request.SeatCreateRequest;
import com.seatliberator.seatliberator.reservation.seat.infrastructure.web.request.SeatUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/seat")
public class SeatController {

    private final CreateSeatUseCase createSeatUseCase;
    private final UpdateSeatUseCase updateSeatUseCase;
    private final DeleteSeatUseCase deleteSeatUseCase;

    @PostMapping
    public Map<String, Boolean> create(
            @RequestBody SeatCreateRequest request
    ) {
        boolean result = createSeatUseCase.create(
                new CreateSeatCommand(
                        request.roomId(),
                        request.seatId()
                )
        );

        return Map.of("success", result);
    }

    @PutMapping
    public Map<String, Boolean> update(
            @RequestBody SeatUpdateRequest request
    ) {
        boolean result = updateSeatUseCase.update(
                new UpdateSeatCommand(
                        request.oldRoomId(),
                        request.oldSeatId(),
                        request.newRoomId(),
                        request.newSeatId()
                )
        );

        return Map.of("success", result);
    }

    @DeleteMapping("/{roomId}/{seatId}")
    public Map<String, Boolean> delete(
            @PathVariable String roomId,
            @PathVariable String seatId
    ) {
        var command = new DeleteSeatCommand(roomId, seatId);
        boolean result = deleteSeatUseCase.delete(command);
        return Map.of("success", result);
    }
}
