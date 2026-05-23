package com.seatliberator.seatliberator.web.user.controller;

import com.seatliberator.seatliberator.identity.server.application.user.port.in.UpdateNicknameUseCase;
import com.seatliberator.seatliberator.identity.server.application.user.port.in.result.UserResult;
import com.seatliberator.seatliberator.web.user.request.UpdateNicknameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UpdateNicknameUseCase updateNicknameUseCase;

    @Operation(summary = "닉네임 변경", description = "특정 사용자의 닉네임을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PatchMapping("/{userId}/nickname")
    public ResponseEntity<UserResult> updateNickname(
            @Parameter(description = "변경할 사용자 Id", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("userId") UUID userId,
            @RequestBody @Valid UpdateNicknameRequest request
    ) {
        var command = request.toCommand(userId);
        var result = updateNicknameUseCase.update(command);
        return ResponseEntity.ok(result);
    }
}
