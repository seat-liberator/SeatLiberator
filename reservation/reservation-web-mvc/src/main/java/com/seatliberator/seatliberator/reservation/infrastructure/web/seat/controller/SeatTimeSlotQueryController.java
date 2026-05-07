package com.seatliberator.seatliberator.reservation.infrastructure.web.seat.controller;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.FindSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.ListSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatTimeSlotQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatTimeSlotQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Seats", description = "스터디룸 좌석 조회 관리 API")
@Slf4j
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class SeatTimeSlotQueryController {

    private final ListSeatTimeSlotUseCase listSeatTimeSlotUseCase;
    private final FindSeatTimeSlotUseCase findSeatTimeSlotUseCase;

    @Operation(summary = "좌석 시간 슬롯 목록 조회", description = "특정 방에 속한 좌석의 시간 슬롯 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/{roomId}/seats/{seatId}/slots")
    public ResponseEntity<List<SeatTimeSlotResult>> listSeatTimeSlots(
            @Parameter(description = "조회할 방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId,
            @Parameter(description = "조회할 좌석 ID", example = "A1")
            @PathVariable("seatId") String seatId
    ) {
        var locator = SimpleSeatLocator.of(roomId, seatId);
        var query = new ListSeatTimeSlotQuery(locator);
        var result = listSeatTimeSlotUseCase.list(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좌석 시간 슬롯 조회", description = "특정 방에 속한 좌석의 특정 시간 슬롯을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/{roomId}/seats/{seatId}/slots/{slotId}")
    public ResponseEntity<SeatTimeSlotResult> findSeatTimeSlot(
            @Parameter(description = "조회할 방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId,
            @Parameter(description = "조회할 좌석 ID", example = "A1")
            @PathVariable("seatId") String seatId,
            @Parameter(description = "좌석 시간 슬롯 ID", example = "00000000-0000-0000-000000000001")
            @PathVariable("seatId") UUID seatTimeSlotId
    ) {
        var query = new FindSeatTimeSlotQuery(seatTimeSlotId);
        var result = findSeatTimeSlotUseCase.find(query);
        return ResponseEntity.ok(result);
    }

}
