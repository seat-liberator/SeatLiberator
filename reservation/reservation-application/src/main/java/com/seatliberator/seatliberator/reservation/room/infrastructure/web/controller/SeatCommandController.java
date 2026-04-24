package com.seatliberator.seatliberator.reservation.room.infrastructure.web.controller;

import com.seatliberator.seatliberator.reservation.room.application.port.in.CreateSeatUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.DeleteSeatUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.UpdateSeatUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.DeleteSeatCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.UpdateSeatCommand;
import com.seatliberator.seatliberator.reservation.room.infrastructure.web.request.SeatCreateRequest;
import com.seatliberator.seatliberator.reservation.room.infrastructure.web.request.SeatUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Seats", description = "스터디룸 좌석 관련 관리 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class SeatCommandController {

    private final CreateSeatUseCase createSeatUseCase;
    private final UpdateSeatUseCase updateSeatUseCase;
    private final DeleteSeatUseCase deleteSeatUseCase;

    @Operation(summary = "좌석 생성", description = "특정 방에 좌석을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PostMapping("/{roomId}/seats")
    public ResponseEntity<?> createSeat(
            @Parameter(description = "방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId,
            @RequestBody SeatCreateRequest request
    ) {
        var command = new CreateSeatCommand(roomId, request.seatId());
        var result = createSeatUseCase.create(command);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좌석 Id 변경", description = "기존 좌석의 식별자를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PutMapping("/{roomId}/seats/{seatId}/id")
    public ResponseEntity<?> updateSeatId(
            @Parameter(description = "방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId,
            @Parameter(description = "좌석 ID", example = "A1")
            @PathVariable("seatId") String seatId,
            @RequestBody SeatUpdateRequest request
    ) {
        var command = new UpdateSeatCommand(roomId, seatId, request.newSeatId());
        var result = updateSeatUseCase.update(command);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좌석 삭제", description = "좌석을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @DeleteMapping("/{roomId}/seats/{seatId}")
    public ResponseEntity<?> deleteSeat(
            @Parameter(description = "방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId,
            @Parameter(description = "좌석 ID", example = "A1")
            @PathVariable("seatId") String seatId
    ) {
        var command = new DeleteSeatCommand(roomId, seatId);
        deleteSeatUseCase.delete(command);
        return ResponseEntity.noContent().build();
    }
}
