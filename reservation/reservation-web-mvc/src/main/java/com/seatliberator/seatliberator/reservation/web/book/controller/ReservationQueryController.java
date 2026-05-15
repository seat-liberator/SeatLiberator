package com.seatliberator.seatliberator.reservation.web.book.controller;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindMyReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindMyReservationQuery;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleInstantRange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@Tag(name = "Reservations", description = "스터디룸 좌석 예약 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationQueryController {

    private final FindMyReservationUseCase findMyReservationUseCase;

    private final ActorContextHolder actorContextHolder;

    @Operation(summary = "내 예약 조회", description = "로그인한 사용자의 예약 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<List<ReservationResult>> me(
            @Parameter(description = "조회 시작 시각", example = "2026-04-20T14:00:00Z")
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startAt,
            @Parameter(description = "조회 종료 시각", example = "2026-04-20T15:00:00Z")
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endAt,
            @Parameter(description = "조회 대상 예약 상태", example = "RESERVED")
            @RequestParam(name = "status") ReservationStatus status
    ) {
        var userId = actorContextHolder.getActor().subject();
        var range = SimpleInstantRange.of(startAt, endAt);
        var query = new FindMyReservationQuery(userId, range, status);
        log.info("내 예약 조회 API 호출됨. 쿼리={}", query);
        var result = findMyReservationUseCase.find(query);
        return ResponseEntity.ok(result);
    }
}
