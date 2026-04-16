package com.seatliberator.seatliberator.reservation.waitlist.application.model;

import com.seatliberator.seatliberator.reservation.domain.WaitlistBehavior;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;
import com.seatliberator.seatliberator.reservation.waitlist.application.internal.WaitlistPromotionResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WaitlistRequests {
    private final List<Waitlist> requests;

    private WaitlistRequests(List<Waitlist> requests) {
        this.requests = List.copyOf(requests);
    }

    public static WaitlistRequests from(List<Waitlist> requests) {
        return new WaitlistRequests(requests);
    }

    public WaitlistProcessingResult process(Instant now, WaitlistPromoter promoter) {
        var groupedByBehavior = requests.stream()
                .collect(Collectors.groupingBy(Waitlist::getBehavior));

        var processedRequests = new ArrayList<Waitlist>();
        var notifications = new ArrayList<WaitlistNotification>();

        processAutoClaim(
                groupedByBehavior.getOrDefault(WaitlistBehavior.AUTO_CLAIM, List.of()),
                now,
                promoter,
                processedRequests,
                notifications
        );
        processNotifyOnly(
                groupedByBehavior.getOrDefault(WaitlistBehavior.NOTIFY_ONLY, List.of()),
                now,
                processedRequests,
                notifications
        );

        return new WaitlistProcessingResult(processedRequests, notifications);
    }

    private void processAutoClaim(
            List<Waitlist> autoClaimRequests,
            Instant now,
            WaitlistPromoter promoter,
            List<Waitlist> processedRequests,
            List<WaitlistNotification> notifications
    ) {
        var sorted = autoClaimRequests.stream()
                .sorted(Comparator.comparing(request -> request.getState().getRequestedAt()))
                .toList();

        for (var request : sorted) {
            processedRequests.add(request);

            var promotionResult = promoter.promote(request);
            if (promotionResult.succeed()) {
                request.complete(now);
                notifications.add(new WaitlistNotification(request, "INFO", "빈 자리를 예약했어요!"));
                break;
            }

            request.fail(now);
            notifications.add(new WaitlistNotification(request, "WARNING", "빈 자리 예약에 실패했어요."));
        }
    }

    private void processNotifyOnly(
            List<Waitlist> notifyOnlyRequests,
            Instant now,
            List<Waitlist> processedRequests,
            List<WaitlistNotification> notifications
    ) {
        for (var request : notifyOnlyRequests) {
            request.complete(now);
            processedRequests.add(request);
            notifications.add(new WaitlistNotification(request, "INFO", "빈 자리가 발생했어요!"));
        }
    }

    @FunctionalInterface
    public interface WaitlistPromoter {
        WaitlistPromotionResult promote(Waitlist request);
    }
}
