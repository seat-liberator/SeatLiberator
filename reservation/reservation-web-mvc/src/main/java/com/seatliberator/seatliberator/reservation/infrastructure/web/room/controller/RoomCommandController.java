package com.seatliberator.seatliberator.reservation.infrastructure.web.room.controller;

import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.DeleteRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.UpdateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.DeleteRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;
import com.seatliberator.seatliberator.reservation.infrastructure.web.room.request.CreateRoomRequest;
import com.seatliberator.seatliberator.reservation.infrastructure.web.room.request.UpdateRoomRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Rooms", description = "스터디룸 방 관련 관리 API")
@Slf4j
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('room.manage')")
public class RoomCommandController {
    private final CreateRoomUseCase createRoomUseCase;
    private final UpdateRoomUseCase updateRoomUseCase;
    private final DeleteRoomUseCase deleteRoomUseCase;

    @Operation(summary = "방 생성", description = "특정 Id의 방을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PostMapping
    public ResponseEntity<RoomResult> createRoom(
            @Valid @RequestBody CreateRoomRequest request
    ) {
        var command = request.toCommand();
        var result = createRoomUseCase.create(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "방 ID 정보 변경", description = "방의 ID를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResult> updateRoomId(
            @Parameter(description = "변경할 방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId,
            @Valid @RequestBody UpdateRoomRequest request
    ) {
        var command = request.toCommand(roomId);
        var result = updateRoomUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "방 삭제", description = "방을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "삭제할 방 ID", example = "study-room-1")
            @PathVariable("roomId") String roomId
    ) {
        var command = new DeleteRoomCommand(roomId);
        deleteRoomUseCase.delete(command);
        return ResponseEntity.noContent().build();
    }
}