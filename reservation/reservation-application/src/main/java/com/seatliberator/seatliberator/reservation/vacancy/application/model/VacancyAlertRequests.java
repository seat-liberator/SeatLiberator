package com.seatliberator.seatliberator.reservation.vacancy.application.model;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestBehavior;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.vacancy.application.internal.VacancyAlertRequestPromotionResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VacancyAlertRequests {
    private final List<VacancyAlertRequest> requests;

    private VacancyAlertRequests(List<VacancyAlertRequest> requests) {
        this.requests = List.copyOf(requests);
    }

    public static VacancyAlertRequests from(List<VacancyAlertRequest> requests) {
        return new VacancyAlertRequests(requests);
    }

    public VacancyAlertProcessingResult process(Instant now, VacancyAlertPromoter promoter) {
        var groupedByBehavior = requests.stream()
                .collect(Collectors.groupingBy(VacancyAlertRequest::getBehavior));

        var processedRequests = new ArrayList<VacancyAlertRequest>();
        var notifications = new ArrayList<VacancyAlertNotification>();

        processAutoClaim(
                groupedByBehavior.getOrDefault(VacancyAlertRequestBehavior.AUTO_CLAIM, List.of()),
                now,
                promoter,
                processedRequests,
                notifications
        );
        processNotifyOnly(
                groupedByBehavior.getOrDefault(VacancyAlertRequestBehavior.NOTIFY_ONLY, List.of()),
                now,
                processedRequests,
                notifications
        );

        return new VacancyAlertProcessingResult(processedRequests, notifications);
    }

    private void processAutoClaim(
            List<VacancyAlertRequest> autoClaimRequests,
            Instant now,
            VacancyAlertPromoter promoter,
            List<VacancyAlertRequest> processedRequests,
            List<VacancyAlertNotification> notifications
    ) {
        var sorted = autoClaimRequests.stream()
                .sorted(Comparator.comparing(request -> request.getState().getRequestedAt()))
                .toList();

        for (var request : sorted) {
            processedRequests.add(request);

            var promotionResult = promoter.promote(request);
            if (promotionResult.succeed()) {
                request.complete(now);
                notifications.add(new VacancyAlertNotification(request, "INFO", "빈 자리를 예약했어요!"));
                break;
            }

            request.fail(now);
            notifications.add(new VacancyAlertNotification(request, "WARNING", "빈 자리 예약에 실패했어요."));
        }
    }

    private void processNotifyOnly(
            List<VacancyAlertRequest> notifyOnlyRequests,
            Instant now,
            List<VacancyAlertRequest> processedRequests,
            List<VacancyAlertNotification> notifications
    ) {
        for (var request : notifyOnlyRequests) {
            request.complete(now);
            processedRequests.add(request);
            notifications.add(new VacancyAlertNotification(request, "INFO", "빈 자리가 발생했어요!"));
        }
    }

    @FunctionalInterface
    public interface VacancyAlertPromoter {
        VacancyAlertRequestPromotionResult promote(VacancyAlertRequest request);
    }
}
