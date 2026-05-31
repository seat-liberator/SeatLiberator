package com.seatliberator.seatliberator.reservation.web.reservation.controller;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.FindReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.ListReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.FindReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.ListReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleInstantRange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Reservations", description = "스터디룸 좌석 예약 관련 API")
@RequestMapping("/api/v1/reservations")
@RestController
@RequiredArgsConstructor
public class ReservationQueryController {

    private final ListReservationUseCase listReservationUseCase;
    private final FindReservationUseCase findReservationUseCase;

    @Operation(summary = "사용자 예약 목록 조회", description = "예약 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping
    public ResponseEntity<List<ReservationResult>> listReservations(
            @Parameter(description = "사용자 ID", example = "00000000-0000-0000-0000-000000000001")
            @RequestParam(name = "userId") String userId,
            @Parameter(description = "예약 상태", example = "RESERVED")
            @RequestParam(name = "status") ReservationStatus status,
            @Parameter(description = "상태 시각 조회 시작", example = "2026-04-14T00:00:00Z")
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable Instant startAt,
            @Parameter(description = "상태 시각 조회 종료", example = "2026-04-15T00:00:00Z")
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable Instant endAt
    ) {
        var statusRange = toStatusRange(startAt, endAt);
        var query = ListReservationQuery.of(userId, status, statusRange);
        var result = listReservationUseCase.list(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 예약 조회", description = "특정 Id의 예약을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResult> findReservation(
            @PathVariable("reservationId") UUID reservationId
    ) {
        var query = FindReservationQuery.of(reservationId);
        var result = findReservationUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    private @Nullable InstantRange toStatusRange(@Nullable Instant startAt, @Nullable Instant endAt) {
        if (startAt == null && endAt == null) {
            return null;
        }

        if (startAt == null || endAt == null) {
            throw new IllegalArgumentException("start and end must be provided together.");
        }

        return SimpleInstantRange.of(startAt, endAt);
    }
}
