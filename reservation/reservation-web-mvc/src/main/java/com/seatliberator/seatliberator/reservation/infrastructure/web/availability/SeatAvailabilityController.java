package com.seatliberator.seatliberator.reservation.infrastructure.web.availability;

import com.seatliberator.seatliberator.reservation.application.availability.port.in.FindAvailableSeatsUseCase;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.FindSeatOccupancyRangesUseCase;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.FindSeatStatusesUseCase;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindAvailableSeatQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindOccupancyRangesQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.query.FindSeatStatusesQuery;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.result.AvailableSeatResult;
import com.seatliberator.seatliberator.reservation.application.availability.port.in.result.SeatStatusesResult;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Seat Availability", description = "스터디룸 좌석 가용 상태 조회 API")
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class SeatAvailabilityController {
    private final FindAvailableSeatsUseCase findAvailableSeatsUseCase;
    private final FindSeatStatusesUseCase findSeatStatusesUseCase;
    private final FindSeatOccupancyRangesUseCase findSeatOccupancyRangesUseCase;

    @Operation(summary = "예약 가능한 좌석 조회", description = "특정 방에서 요청한 시간대에 예약 가능한 좌석 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/{roomId}/available-seats")
    public ResponseEntity<List<AvailableSeatResult>> getAvailableSeats(
            @Parameter(description = "방 ID", example = "room-1")
            @PathVariable("roomId") String roomId,
            @Parameter(description = "조회 시작 시각", example = "2026-04-20T13:00:00Z")
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startAt,
            @Parameter(description = "조회 종료 시각", example = "2026-04-20T14:30:00Z")
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endAt
    ) {
        var range = SimpleTimeRange.of(startAt, endAt);
        var query = new FindAvailableSeatQuery(roomId, range);
        var result = findAvailableSeatsUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좌석별 점유 상태 조회", description = "특정 방의 좌석별 예약 점유 상태를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/{roomId}/seat-statuses")
    public ResponseEntity<List<SeatStatusesResult>> getSeatStatuses(
            @Parameter(description = "방 ID", example = "room-1")
            @PathVariable("roomId") String roomId,
            @Parameter(description = "조회 시작 시각", example = "2026-04-20T13:00:00Z")
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startAt,
            @Parameter(description = "조회 종료 시각", example = "2026-04-20T14:30:00Z")
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endAt
    ) {
        var range = SimpleTimeRange.of(startAt, endAt);
        var query = new FindSeatStatusesQuery(roomId, range);
        var result = findSeatStatusesUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좌석 점유 구간 조회", description = "특정 좌석이 요청한 기간 안에서 실제로 점유된 시간 구간을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "좌석 없음")
    })
    @GetMapping("/{roomId}/seats/{seatId}/occupy")
    public ResponseEntity<?> getSeatOccupiedRange(
            @Parameter(description = "방 ID", example = "room-1")
            @PathVariable("roomId") String roomId,
            @Parameter(description = "좌석 ID", example = "A-1")
            @PathVariable("seatId") String seatId,
            @Parameter(description = "조회 시작 시각", example = "2026-04-20T13:00:00Z")
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startAt,
            @Parameter(description = "조회 종료 시각", example = "2026-04-20T14:30:00Z")
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endAt
    ) {
        var locator = SimpleSeatLocator.of(roomId, seatId);
        var range = SimpleTimeRange.of(startAt, endAt);
        var query = new FindOccupancyRangesQuery(locator, range);
        var result = findSeatOccupancyRangesUseCase.find(query);
        return ResponseEntity.ok(result);
    }
}
