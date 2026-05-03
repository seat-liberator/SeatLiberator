package com.seatliberator.seatliberator.reservation.application.waitlist.model;

import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;

import java.util.List;

public record WaitlistProcessingResult(
        List<Waitlist> requestsToSave,
        List<WaitlistNotification> notifications
) {
    public WaitlistProcessingResult {
        requestsToSave = List.copyOf(requestsToSave);
        notifications = List.copyOf(notifications);
    }

    public boolean isEmpty() {
        return requestsToSave.isEmpty() && notifications.isEmpty();
    }
}
