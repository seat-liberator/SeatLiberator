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
@RequestMapping("/rooms")
public class SeatController {

    private final CreateSeatUseCase createSeatUseCase;
    private final UpdateSeatUseCase updateSeatUseCase;
    private final DeleteSeatUseCase deleteSeatUseCase;

    @PostMapping("/{roomId}/seats")
    public Map<String, Boolean> create(
            @PathVariable("roomId") String roomId,
            @RequestBody SeatCreateRequest request
    ) {
        var command = new CreateSeatCommand(roomId, request.seatId());
        boolean result = createSeatUseCase.create(command);

        return Map.of("success", result);
    }

    @PutMapping("/{roomId}/seats/{seatId}")
    public Map<String, Boolean> update(
            @PathVariable("roomId") String roomId,
            @PathVariable("seatId") String seatId,
            @RequestBody SeatUpdateRequest request
    ) {
        var command = new UpdateSeatCommand(roomId, seatId, request.newRoomId(), request.newSeatId());
        boolean result = updateSeatUseCase.update(command);

        return Map.of("success", result);
    }

    @DeleteMapping("/{roomId}/seats/{seatId}")
    public Map<String, Boolean> delete(
            @PathVariable("roomId") String roomId,
            @PathVariable("seatId") String seatId
    ) {
        var command = new DeleteSeatCommand(roomId, seatId);
        boolean result = deleteSeatUseCase.delete(command);
        return Map.of("success", result);
    }
}
