package com.seatliberator.seatliberator.reservation.infrastructure.web.waitlist.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.infrastructure.web.waitlist.request.CreateWaitlistRequest;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.CancelWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.CreateWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CancelWaitlistCommand;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CreateWaitlistCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Waitlist", description = "좌석 대기열 요청 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/waitlist")
public class WaitlistController {

    private final CreateWaitlistUseCase createWaitlistUseCase;
    private final CancelWaitlistUseCase cancelWaitlistUseCase;

    private final ActorContextHolder actorContextHolder;

    @Operation(summary = "대기열 등록", description = "예약이 불가능한 좌석에 대해 대기열 요청을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody CreateWaitlistRequest request
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new CreateWaitlistCommand(
                userId,
                request.roomId(),
                request.seatId(),
                request.startAt(),
                request.endAt(),
                request.behavior()
        );

        createWaitlistUseCase.create(command);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "대기열 취소", description = "로그인한 사용자의 기존 대기열 요청을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "대기열 요청 없음")
    })
    @DeleteMapping("/{waitlistId}")
    public ResponseEntity<Void> cancel(
            @Parameter(description = "대기열 요청 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
            @PathVariable UUID waitlistId
    ) {
        var userId = actorContextHolder.getActor().subject();

        var command = new CancelWaitlistCommand(userId, waitlistId);

        cancelWaitlistUseCase.cancel(command);

        return ResponseEntity.noContent().build();
    }
}
