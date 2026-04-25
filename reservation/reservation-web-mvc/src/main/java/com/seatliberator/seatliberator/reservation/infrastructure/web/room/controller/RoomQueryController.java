package com.seatliberator.seatliberator.reservation.infrastructure.web.room.controller;

import com.seatliberator.seatliberator.reservation.application.room.port.in.FindRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.ListRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.query.FindRoomQuery;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;
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

@Tag(name = "Rooms", description = "스터디룸 방 조회 관리 API")
@Slf4j
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomQueryController {
    private final ListRoomUseCase listRoomUseCase;
    private final FindRoomUseCase findRoomUseCase;

    @Operation(summary = "방 목록 조회", description = "방 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('room.list')")
    public ResponseEntity<List<RoomResult>> findRooms() {
        var result = listRoomUseCase.list();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 방 조회", description = "특정 Id 방의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/{roomId}")
    @PreAuthorize("hasAuthority('room.read')")
    public ResponseEntity<RoomResult> findRoom(
            @Parameter(description = "조회할 방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId
    ) {
        var query = new FindRoomQuery(roomId);
        var result = findRoomUseCase.find(query);
        return ResponseEntity.ok(result);
    }
}
