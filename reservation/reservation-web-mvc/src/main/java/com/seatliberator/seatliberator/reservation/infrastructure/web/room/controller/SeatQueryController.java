package com.seatliberator.seatliberator.reservation.infrastructure.web.room.controller;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.FindSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.ListSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Seats", description = "스터디룸 좌석 조회 관리 API")
@Slf4j
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class SeatQueryController {
    private final ListSeatUseCase listSeatUseCase;
    private final FindSeatUseCase findSeatUseCase;

    @Operation(summary = "좌석 목록 조회", description = "특정 방의 좌석 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/{roomId}/seats")
    @PreAuthorize("hasAuthority('seat.list')")
    public ResponseEntity<List<SeatResult>> listSeatsInRoom(
            @Parameter(description = "조회할 방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId
    ) {
        var query = new ListSeatQuery(roomId);
        var result = listSeatUseCase.list(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좌석 조회", description = "특정 Id 좌석의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/{roomId}/seats/{seatId}")
    @PreAuthorize("hasAuthority('seat.read')")
    public ResponseEntity<SeatResult> findSeatInRoom(
            @Parameter(description = "조회할 방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId,
            @Parameter(description = "조회할 좌석 ID", example = "A1")
            @PathVariable("seatId") String seatId
    ) {
        var query = new FindSeatQuery(roomId, seatId);
        var result = findSeatUseCase.find(query);
        return ResponseEntity.ok(result);
    }
}
