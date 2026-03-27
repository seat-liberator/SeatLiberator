package com.seatliberator.seatliberator.reservation.infrastructure.web.controller;

import com.seatliberator.seatliberator.reservation.infrastructure.web.response.VacancyAlertNotificationResponse;
import com.seatliberator.seatliberator.vacancy.application.port.in.VacancyAlertNotificationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancy-alert/notifications")
public class VacancyAlertNotificationController {
    private final VacancyAlertNotificationReader notificationReader;

    @GetMapping("/{userId}")
    public List<VacancyAlertNotificationResponse> getMyNotifications(
            @PathVariable String userId,
            @RequestHeader("X-Actor-Id") String actorId
    ) {
        return notificationReader.readAllByUser(userId, actorId).stream()
                .map(VacancyAlertNotificationResponse::from)
                .toList();
    }
}
