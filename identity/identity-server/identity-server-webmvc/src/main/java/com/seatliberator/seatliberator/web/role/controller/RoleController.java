package com.seatliberator.seatliberator.web.role.controller;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.GrantRoleUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.RevokeRoleUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.UpdateRoleUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.UserGrantedRoleResult;
import com.seatliberator.seatliberator.web.role.request.GrantRoleRequest;
import com.seatliberator.seatliberator.web.role.request.RevokeRoleRequest;
import com.seatliberator.seatliberator.web.role.request.UpdateRoleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Role", description = "역할 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final GrantRoleUseCase grantRoleUseCase;
    private final RevokeRoleUseCase revokeRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;

    @Operation(summary = "권한 부여", description = "특정 사용자에게 역할을 부여합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "부여 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PostMapping
    public ResponseEntity<UserGrantedRoleResult> grantRole(
            @RequestBody @Valid GrantRoleRequest request
    ) {
        var command = request.toCommand();
        var result = grantRoleUseCase.grant(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "권한 수정", description = "특정 사용자에게 부여된 역할을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PatchMapping
    public ResponseEntity<UserGrantedRoleResult> updateRole(
            @RequestBody @Valid UpdateRoleRequest request
    ) {
        var command = request.toCommand();
        var result = updateRoleUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "권한 회수", description = "특정 사용자가 갖고 있는 역할을 회수합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회수 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @DeleteMapping
    public ResponseEntity<Void> revokeRole(
            @RequestBody @Valid RevokeRoleRequest request
    ) {
        var command = request.toCommand();
        revokeRoleUseCase.revoke(command);
        return ResponseEntity.noContent().build();
    }


}
