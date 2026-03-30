package com.seatliberator.seatliberator.notification.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.notification.application.port.in.NotificationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationReader notificationReader;
    private final ActorContextHolder actorContextHolder;

    @GetMapping("/notification")
    public ResponseEntity<?> getNotification() {
        var userId = actorContextHolder.getActor().subject();
        var result = notificationReader.readByTargetUserId(userId);
        return ResponseEntity.ok(result);
    }
}
