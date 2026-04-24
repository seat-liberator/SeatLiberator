package com.seatliberator.seatliberator.reservation.book.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.infrastructure.web.request.ReservationCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/rooms")
public class CreateReservationController {
    private final CreateReservationUseCase createReservationUseCase;
    private final ActorContextHolder actorContextHolder;

    @Operation(summary = "예약 생성", description = "특정 방의 특정 좌석에 대해서 예약을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PostMapping("/{roomId}/seats/{seatId}/reservations")
    public ResponseEntity<?> create(
            @Parameter(description = "방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId,
            @Parameter(description = "좌석 ID", example = "A1")
            @PathVariable("seatId") String seatId,
            @RequestBody ReservationCreateRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new CreateReservationCommand(
                userId,
                roomId,
                seatId,
                request.startAt(),
                request.endAt()
        );
        var result = createReservationUseCase.create(command);

        return ResponseEntity.ok(result);
    }
}
