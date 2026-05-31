package com.seatliberator.seatliberator.reservation.web.booking.controller;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindAvailableSlotsBySeatUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindAvailableSlotsBySeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDateRange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Booking", description = "스터디룸 좌석 예약 관련 API")
@RequestMapping("/api/v1/booking")
@RestController
@RequiredArgsConstructor
public class AvailabilityQueryController {

    private final FindAvailableSlotsBySeatUseCase findAvailableSlotsBySeatUseCase;

    @Operation(summary = "좌석 예약 가능 슬롯 조회", description = "특정 좌석의 날짜별 예약 가능한 시간 슬롯 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/seats/{seatId}/available-slots")
    public ResponseEntity<Map<LocalDate, List<SeatTimeSlotResult>>> findAvailableSlotsBySeat(
            @Parameter(description = "좌석 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("seatId") UUID seatId,
            @Parameter(description = "조회 시작일", example = "2026-05-18")
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startAt,
            @Parameter(description = "조회 종료일", example = "2026-05-20")
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endAt
    ) {
        var range = SimpleDateRange.of(startAt, endAt);
        var query = FindAvailableSlotsBySeatQuery.of(seatId, range);
        var result = findAvailableSlotsBySeatUseCase.findAtDateRange(query);
        return ResponseEntity.ok(result);
    }
}
