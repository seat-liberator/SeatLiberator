package com.seatliberator.seatliberator.reservation.vacancy.application.handler;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestStatus;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationCanceled;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationExpired;
import com.seatliberator.seatliberator.reservation.shared.application.notifier.Notifier;
import com.seatliberator.seatliberator.reservation.vacancy.application.internal.VacancyAlertRequestPromotion;
import com.seatliberator.seatliberator.reservation.vacancy.application.model.VacancyAlertNotification;
import com.seatliberator.seatliberator.reservation.vacancy.application.model.VacancyAlertRequests;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.result.VacancyAlertRequestResult;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class SeatVacancyHandler {
    private final VacancyAlertRequestStore store;
    private final VacancyAlertRequestPromotion promotion;

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
        var requests = store.findByLocatorAndRangeAndStatus(locator, range, VacancyAlertRequestStatus.ACTIVE);
        var processingResult = VacancyAlertRequests.from(requests)
                .process(clock.instant(), request -> promotion.promote(request.getUserId(), request.getLocator(), request.getRange()));

        if (processingResult.isEmpty()) {
            return;
        }

        store.saveAll(processingResult.requestsToSave());
        processingResult.notifications().forEach(this::notify);
    }

    private void notify(VacancyAlertNotification notification) {
        notifier.consume(
                notification.userId(),
                notification.level(),
                notification.title(),
                VacancyAlertRequestResult.from(notification.request())
        );
    }
}
