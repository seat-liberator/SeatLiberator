package com.seatliberator.seatliberator.reservation.application.waitlist.model;

import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;

public record WaitlistNotification(
        Waitlist request,
        String level,
        String title
) {
    public String userId() {
        return request.getUserId();
    }
}
