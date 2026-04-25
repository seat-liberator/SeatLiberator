package com.seatliberator.seatliberator.reservation.application.waitlist.handler;

import com.seatliberator.seatliberator.reservation.application.shared.notifier.Notifier;
import com.seatliberator.seatliberator.reservation.application.waitlist.internal.WaitlistPromotion;
import com.seatliberator.seatliberator.reservation.application.waitlist.model.WaitlistNotification;
import com.seatliberator.seatliberator.reservation.application.waitlist.model.WaitlistRequests;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.result.WaitlistResult;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.WaitlistStatus;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationCanceled;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationExpired;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class SeatVacancyHandler {
    private final WaitlistStore store;
    private final WaitlistPromotion promotion;

    private final Notifier notifier;
    private final Clock clock;

    @EventListener
    public void handle(ReservationCanceled event) {
        var locator = event.locator();
        var range = event.range();

        processVacancy(locator, range);
    }

    @EventListener
    public void handle(ReservationExpired event) {
        var locator = event.locator();
        var range = event.range();

        processVacancy(locator, range);
    }

    private void processVacancy(SeatLocator locator, TimeRange range) {
        var requests = store.findByLocatorAndRangeAndStatus(locator, range, WaitlistStatus.ACTIVE);
        var processingResult = WaitlistRequests.from(requests)
                .process(clock.instant(), request -> promotion.promote(request.getUserId(), request.getLocator(), request.getRange()));

        if (processingResult.isEmpty()) {
            return;
        }

        store.saveAll(processingResult.requestsToSave());
        processingResult.notifications().forEach(this::notify);
    }

    private void notify(WaitlistNotification notification) {
        notifier.consume(
                notification.userId(),
                notification.level(),
                notification.title(),
                WaitlistResult.from(notification.request())
        );
    }
}
