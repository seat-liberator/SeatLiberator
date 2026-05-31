package com.seatliberator.seatliberator.reservation.web.seat.controller;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.CreateSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.DeleteSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.UpdateSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.DeleteSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.web.seat.request.SeatTimeSlotCreateRequest;
import com.seatliberator.seatliberator.reservation.web.seat.request.SeatTimeSlotUpdateRequest;
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
public class SeatTimeSlotCommandController {

    private final CreateSeatTimeSlotUseCase createSeatTimeSlotUseCase;
    private final UpdateSeatTimeSlotUseCase updateSeatTimeSlotUseCase;
    private final DeleteSeatTimeSlotUseCase deleteSeatTimeSlotUseCase;

    @Operation(summary = "좌석 시간 슬롯 생성", description = "좌석의 예약 가능한 시간 슬롯을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PostMapping("/{roomId}/seats/{seatId}/slots")
    public ResponseEntity<SeatTimeSlotResult> createSeatTimeSlot(
            @Parameter(description = "방 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("roomId") UUID roomId,
            @Parameter(description = "좌석 ID", example = "00000000-0000-0000-0000-000000000002")
            @PathVariable("seatId") UUID seatId,
            @Valid @RequestBody SeatTimeSlotCreateRequest request
    ) {
        var command = request.toCommand(seatId);
        var result = createSeatTimeSlotUseCase.create(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좌석 시간 슬롯 변경", description = "좌석의 예약 가능한 시간 슬롯 구간을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PutMapping("/{roomId}/seats/{seatId}/slots/{slotId}")
    public ResponseEntity<SeatTimeSlotResult> updateSeatTimeSlot(
            @Parameter(description = "방 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("roomId") UUID roomId,
            @Parameter(description = "좌석 ID", example = "00000000-0000-0000-0000-000000000002")
            @PathVariable("seatId") UUID seatId,
            @Parameter(description = "좌석 시간 슬롯 ID", example = "00000000-0000-0000-0000-000000000003")
            @PathVariable("slotId") UUID seatTimeSlotId,
            @Valid @RequestBody SeatTimeSlotUpdateRequest request
    ) {
        var command = request.toCommand(seatTimeSlotId);
        var result = updateSeatTimeSlotUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좌석 시간 슬롯 삭제", description = "좌석 시간 슬롯을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @DeleteMapping("/{roomId}/seats/{seatId}/slots/{slotId}")
    public ResponseEntity<Void> deleteSeatTimeSlot(
            @Parameter(description = "방 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("roomId") UUID roomId,
            @Parameter(description = "좌석 ID", example = "00000000-0000-0000-0000-000000000002")
            @PathVariable("seatId") UUID seatId,
            @Parameter(description = "좌석 시간 슬롯 ID", example = "00000000-0000-0000-0000-000000000003")
            @PathVariable("slotId") UUID seatTimeSlotId
    ) {
        var command = new DeleteSeatTimeSlotCommand(seatTimeSlotId);
        deleteSeatTimeSlotUseCase.delete(command);
        return ResponseEntity.noContent().build();
    }
}
