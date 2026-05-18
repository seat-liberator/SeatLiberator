package com.seatliberator.seatliberator.reservation.web.reservation.controller;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.UseReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Reservations", description = "스터디룸 좌석 예약 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class UseReservationController {
    private final UseReservationUseCase useReservationUseCase;

    @Operation(summary = "예약 사용", description = "특정 예약을 사용 처리 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PostMapping("/{reservationId}")
    public ResponseEntity<ReservationResult> use(
            @Parameter(description = "예약 ID", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("reservationId") UUID reservationId
    ) {
        var command = UseReservationCommand.of(reservationId);
        var result = useReservationUseCase.use(command);
        return ResponseEntity.ok(result);
    }
}
