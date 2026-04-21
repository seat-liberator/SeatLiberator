package com.seatliberator.seatliberator.notification.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.notification.application.port.in.NotificationReader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notifications", description = "사용자 알림 조회 API")
@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationReader notificationReader;
    private final ActorContextHolder actorContextHolder;

    @Operation(summary = "내 알림 조회", description = "로그인한 사용자에게 전달된 알림 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/notification")
    public ResponseEntity<?> getNotification() {
        var userId = actorContextHolder.getActor().subject();
        var result = notificationReader.readByTargetUserId(userId);
        return ResponseEntity.ok(result);
    }
}
