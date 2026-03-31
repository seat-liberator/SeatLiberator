package com.seatliberator.seatliberator.reservation.vacancy.application.event.handler;

import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventPublisher;
import com.seatliberator.seatliberator.notification.api.event.NotificationCreateRequestEventPayload;
import com.seatliberator.seatliberator.notification.api.event.NotificationEventType;
import com.seatliberator.seatliberator.reservation.api.event.VacancyAlertNotificationPayload;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertStatus;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationCanceled;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class ReservationCanceledHandler {
    private final VacancyAlertRequestReader reader;
    private final EventTraceHolder eventTraceHolder;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @EventListener
    public void handle(ReservationCanceled event) {
        var locator = event.locator();
        var range = event.range();
        var requests = reader.findByLocatorAndRangeAndStatus(locator, range, VacancyAlertStatus.ACTIVE);

        for (var request : requests) {
            var targetLocator = request.getLocator();
            var targetRange = request.getRange();
            var payloadBody = new VacancyAlertNotificationPayload(
                    targetLocator.roomId(),
                    targetLocator.seatId(),
                    targetRange.startAt(),
                    targetRange.endAt()
            );
            var stringifiedBody = objectMapper.writeValueAsString(payloadBody);
            var notificationEventPayload = new NotificationCreateRequestEventPayload(request.getUserId(), "INFO", "빈자리 발생!", stringifiedBody);

            eventTraceHolder.with(
                    () -> eventPublisher.publish(NotificationEventType.NOTIFICATION_CREATE_REQUEST, notificationEventPayload),
                    state -> state.withAggregate("vacancy alert request", request.getId().toString())
            );

            request.fulfill(clock.instant());
        }
    }
}
