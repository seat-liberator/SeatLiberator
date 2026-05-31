package com.seatliberator.seatliberator.reservation.web.seat.controller;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.CreateSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.DeleteSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.UpdateSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.DeleteSeatCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;
import com.seatliberator.seatliberator.reservation.web.seat.request.SeatCreateRequest;
import com.seatliberator.seatliberator.reservation.web.seat.request.SeatUpdateCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Seats", description = "스터디룸 좌석 관련 관리 API")
@RequestMapping("/api/v1/rooms")
@RestController
@RequiredArgsConstructor
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
    public ResponseEntity<SeatResult> createSeat(
            @Parameter(description = "좌석을 생성할 방 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("roomId") UUID roomId,
            @Valid @RequestBody SeatCreateRequest request
    ) {
        var command = request.toCommand(roomId);
        var result = createSeatUseCase.create(command);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좌석 Code 변경", description = "기존 좌석의 Code를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PutMapping("/{roomId}/seats/{seatId}/id")
    public ResponseEntity<SeatResult> updateSeatId(
            @Parameter(description = "좌석이 속한 방 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("roomId") UUID roomId,
            @Parameter(description = "Code를 변경할 좌석 ID", example = "00000000-0000-0000-0000-000000000002")
            @PathVariable("seatId") UUID seatId,
            @Valid @RequestBody SeatUpdateCodeRequest request
    ) {
        var command = request.toCommand(seatId);
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
    public ResponseEntity<Void> deleteSeat(
            @Parameter(description = "좌석이 속한 방 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("roomId") UUID roomId,
            @Parameter(description = "좌석 ID", example = "00000000-0000-0000-0000-000000000002")
            @PathVariable("seatId") UUID seatId
    ) {
        var command = DeleteSeatCommand.of(seatId);
        deleteSeatUseCase.delete(command);
        return ResponseEntity.noContent().build();
    }
}
