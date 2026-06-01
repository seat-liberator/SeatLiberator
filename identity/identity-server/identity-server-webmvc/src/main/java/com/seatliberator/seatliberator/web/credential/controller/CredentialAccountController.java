package com.seatliberator.seatliberator.web.credential.controller;

import com.seatliberator.seatliberator.identity.server.application.credential.port.in.UpdatePasswordUseCase;
import com.seatliberator.seatliberator.web.credential.request.UpdatePasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account", description = "계정 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class CredentialAccountController {
    private final UpdatePasswordUseCase updatePasswordUseCase;

    @Operation(summary = "비밀번호 변경", description = "특정 사용자의 비밀번호를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "부여 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody @Valid UpdatePasswordRequest request
    ) {
        var command = request.toCommand();
        updatePasswordUseCase.update(command);
        return ResponseEntity.noContent().build();
    }
}
