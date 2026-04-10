package com.seatliberator.seatliberator.reservation.vacancy.application.handler;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestBehavior;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestStatus;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationCanceled;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationExpired;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.shared.application.notifier.Notifier;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequestPromotion;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.entry.VacancyAlertRequestEntry;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        var groupByAction = requests.stream().collect(Collectors.groupingBy(VacancyAlertRequest::getBehavior));

        var autoClaim = groupByAction.getOrDefault(VacancyAlertRequestBehavior.AUTO_CLAIM, List.of());
        var notifyOnly = groupByAction.getOrDefault(VacancyAlertRequestBehavior.NOTIFY_ONLY, List.of());

        processAutoClaim(autoClaim);
        processNotifyOnly(notifyOnly);
    }

    private void processAutoClaim(List<VacancyAlertRequest> requests) {
        var now = clock.instant();
        var sorted = requests.stream()
                .sorted(Comparator.comparing(request -> request.getState().getRequestedAt()))
                .toList();

        var processedRequests = new ArrayList<VacancyAlertRequest>();

        for (var request : sorted) {
            processedRequests.add(request);
            var promotionResult = promotion.promote(request.getUserId(), request.getLocator(), request.getRange());
            if (promotionResult.succeed()) {
                request.complete(now);
                notifier.consume(request.getUserId(), "INFO", "빈 자리를 예약했어요!", VacancyAlertRequestEntry.from(request));
                break;
            } else {
                request.fail(now);
                notifier.consume(request.getUserId(), "WARNING", "빈 자리 예약에 실패했어요.", VacancyAlertRequestEntry.from(request));
            }
        }

        store.saveAll(processedRequests);
    }

    private void processNotifyOnly(List<VacancyAlertRequest> requests) {
        var now = clock.instant();
        for (var request : requests) {
            request.complete(now);
            notifier.consume(request.getUserId(), "INFO", "빈 자리가 발생했어요!", VacancyAlertRequestEntry.from(request));
            store.save(request);
        }
    }
}
