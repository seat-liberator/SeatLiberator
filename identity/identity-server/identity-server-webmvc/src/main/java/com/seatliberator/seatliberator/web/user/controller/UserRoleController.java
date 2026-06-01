package com.seatliberator.seatliberator.web.user.controller;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.FindUserGrantedSummaryUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.query.FindUserGrantedSummaryQuery;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.UserGrantedSummaryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRoleController {
    private final FindUserGrantedSummaryUseCase findUserGrantedSummaryUseCase;

    @Operation(summary = "사용자 권한 조회", description = "특정 사용자에게 부여된 권한을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/{userId}/roles")
    public ResponseEntity<UserGrantedSummaryResult> findUserGranted(
            @Parameter(description = "조회할 사용자 Id", example = "00000000-0000-0000-0000-000000000001")
            @PathVariable("userId") UUID userId
    ) {
        var query = FindUserGrantedSummaryQuery.of(userId);
        var result = findUserGrantedSummaryUseCase.find(query);
        return ResponseEntity.ok(result);
    }
}
