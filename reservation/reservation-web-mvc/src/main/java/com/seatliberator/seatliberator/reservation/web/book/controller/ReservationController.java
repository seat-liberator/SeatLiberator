package com.seatliberator.seatliberator.reservation.web.book.controller;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CancelReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.UpdateReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CancelReservationCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.UpdateReservationCommand;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.web.book.request.ReservationUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservations", description = "스터디룸 좌석 예약 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {
    private final UpdateReservationUseCase updateReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;

    private final ActorContextHolder actorContextHolder;

    @Operation(summary = "예약 변경", description = "이미 생성된 기존 예약의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PutMapping
    public ResponseEntity<ReservationResult> update(
            @RequestBody ReservationUpdateRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new UpdateReservationCommand(
                userId,
                request.roomId(),
                request.seatId(),
                request.startAt(),
                request.endAt()
        );
        var result = updateReservationUseCase.update(command);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "예약 취소", description = "이미 생성된 기존 예약을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @DeleteMapping
    public ResponseEntity<ReservationResult> delete() {
        var userId = actorContextHolder.getActor().subject();
        var command = new CancelReservationCommand(userId);
        var result = cancelReservationUseCase.cancel(command);
        return ResponseEntity.ok(result);
    }
}
