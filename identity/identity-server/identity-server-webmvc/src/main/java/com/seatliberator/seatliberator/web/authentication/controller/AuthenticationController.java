package com.seatliberator.seatliberator.web.authentication.controller;

import com.seatliberator.seatliberator.identity.server.application.token.port.in.RefreshAccessTokenUseCase;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.result.AccessTokenResult;
import com.seatliberator.seatliberator.web.authentication.request.RefreshAccessTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @Operation(summary = "토큰 재발급", description = "세션을 유지할 수 있도록 토큰을 재발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResult> refresh(
            @RequestBody @Valid RefreshAccessTokenRequest request
    ) {
        var command = request.toCommand();
        var result = refreshAccessTokenUseCase.refresh(command);
        return ResponseEntity.ok(result);
    }
}
