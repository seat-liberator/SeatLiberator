package com.seatliberator.seatliberator.reservation.vacancy.application.event.handler;

import com.seatliberator.seatliberator.eventrelay.core.factory.EventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.relay.outbound.EventPublisher;
import com.seatliberator.seatliberator.notification.api.event.NotificationCreateRequestEventPayload;
import com.seatliberator.seatliberator.notification.api.event.NotificationEventType;
import com.seatliberator.seatliberator.reservation.api.event.VacancyAlertNotificationPayload;
import com.seatliberator.seatliberator.reservation.book.application.event.payload.ReservationCanceledEvent;
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
    public void handle(ReservationCanceledEvent event) {
        var requests = reader.findActiveRequest(
                event.roomId(),
                event.seatId(),
                event.startAt(),
                event.endAt());

        for (var request : requests) {
            var locator = request.getLocator();
            var range = request.getRange();
            var payloadBody = new VacancyAlertNotificationPayload(
                    locator.roomId(),
                    locator.seatId(),
                    range.startAt(),
                    range.endAt()
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
