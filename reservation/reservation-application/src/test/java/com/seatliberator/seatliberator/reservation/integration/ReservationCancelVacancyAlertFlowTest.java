package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.notification.api.event.NotificationCreateRequestEventPayload;
import com.seatliberator.seatliberator.notification.api.event.NotificationEventType;
import com.seatliberator.seatliberator.reservation.VacancyAlertRequestCreateCommandBuilder;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CancelReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CancelReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.domain.*;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationExpired;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.seat.application.service.SeatCommandService;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.RequestVacancyAlertUseCase;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.result.VacancyAlertRequestResult;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@TransactionalReservationIntegrationTest
@DisplayName("Integration: Reservation Cancel Vacancy Alert Flow")
public class ReservationCancelVacancyAlertFlowTest extends ReservationDatabaseCleanupSupport {
    private static final Instant BASE_TIME = Instant.parse("2026-01-01T00:00:00Z");

    @Autowired
    SeatCommandService seatService;

    @Autowired
    CreateReservationUseCase createReservationUseCase;

    @Autowired
    CancelReservationUseCase cancelReservationUseCase;

    @Autowired
    ReservationStore reservationStore;

    @Autowired
    ReservationReader reservationReader;

    @Autowired
    CreateWaitlistUseCase createWaitlistUseCase;

    @Autowired
    WaitlistStore waitlistStore;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    ReservationIntegrationTestConfiguration.TestEventPublisher testEventPublisher;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void clearPublishedEvents() {
        testEventPublisher.clear();
    }

    @Test
    @DisplayName("예약 취소 이벤트 발생 시 유효한 NOTIFY_ONLY 요청에만 알림 생성 흐름이 연결된다")
    void notify_only_request_is_notified_when_reservation_is_canceled() throws Exception {
        var target = createReservedSeat("cancel-notify", "reservation-user");
        var other = createReservedSeat("cancel-notify-other", "other-reservation-user");
        var activeRequest = requestVacancy(target, "active-user", WaitlistBehavior.NOTIFY_ONLY);
        var canceledRequest = requestVacancy(target, "canceled-user", WaitlistBehavior.NOTIFY_ONLY);
        var otherSeatRequest = requestVacancy(other, "other-seat-user", WaitlistBehavior.NOTIFY_ONLY);
        createWaitlistUseCase.cancel(new CancelWaitlistCommand(canceledRequest.getUserId(), canceledRequest.getId()));

        var command = new CancelReservationCommand(target.reservationUserId());
        cancelReservationUseCase.cancel(command);

        assertSingleNotification(activeRequest, "active-user", "INFO", "빈 자리가 발생했어요!");
        assertVacancyAlertRequestState(activeRequest, WaitlistStatus.COMPLETED, WaitlistResolution.NOTIFIED);
        assertVacancyAlertRequestState(canceledRequest, WaitlistStatus.CANCELLED, WaitlistResolution.PENDING);
        assertVacancyAlertRequestState(otherSeatRequest, WaitlistStatus.ACTIVE, WaitlistResolution.PENDING);
    }

    @Test
    @DisplayName("예약 만료 이벤트 발생 시 AUTO_CLAIM 요청은 실제 예약으로 승격되고 알림이 생성된다")
    void auto_claim_request_is_promoted_and_notified_when_reservation_expired_event_is_published() throws Exception {
        var target = createSeatWithoutReservation("expired-auto-claim");
        var autoClaimRequest = saveVacancyAlertRequest("expired-auto-claim-user", target, WaitlistBehavior.AUTO_CLAIM);

        applicationEventPublisher.publishEvent(new ReservationExpired(target.locator(), target.range(), BASE_TIME));

        assertSingleNotification(autoClaimRequest, "expired-auto-claim-user", "INFO", "빈 자리를 예약했어요!");
        assertVacancyAlertRequestState(autoClaimRequest, WaitlistStatus.COMPLETED, WaitlistResolution.CLAIMED);
        assertReservationCreated("expired-auto-claim-user", target.locator(), target.range());
    }

    private SeatVacancyTarget createReservedSeat(String name, String reservationUserId) {
        var target = createSeatWithoutReservation(name);
        var command = new CreateReservationCommand(
                reservationUserId,
                target.locator().roomId(),
                target.locator().seatId(),
                target.range().startAt(),
                target.range().endAt()

        );
        createReservationUseCase.create(command);
        return new SeatVacancyTarget(reservationUserId, target.locator(), target.range());
    }

    private SeatVacancyTarget createSeatWithoutReservation(String name) {
        var locator = createLocator("room-" + name, "seat-" + name);
        var range = createRange(BASE_TIME.plusSeconds(60), BASE_TIME.plusSeconds(120));
        seatService.create(new CreateSeatCommand(locator.roomId(), locator.seatId()));
        return new SeatVacancyTarget(null, locator, range);
    }

    private Waitlist requestVacancy(
            SeatVacancyTarget target,
            String userId,
            WaitlistBehavior behavior
    ) {
        var command = new VacancyAlertRequestCreateCommandBuilder()
                .userId(userId)
                .locator(target.locator())
                .range(target.range())
                .behavior(behavior)
                .build();
        return createWaitlistUseCase.create(command);
    }

    private Waitlist saveVacancyAlertRequest(
            String userId,
            SeatVacancyTarget target,
            WaitlistBehavior behavior
    ) {
        var request = Waitlist.create(userId, target.locator(), target.range(), behavior, BASE_TIME);
        return waitlistStore.save(request);
    }

    private void assertSingleNotification(
            Waitlist request,
            String targetUserId,
            String level,
            String title
    ) throws Exception {
        var publishedEvents = testEventPublisher.publishedEvents();
        assertThat(publishedEvents).singleElement().satisfies(event -> {
            assertThat(event.type()).isEqualTo(NotificationEventType.NOTIFICATION_CREATE_REQUEST);
            assertThat(event.payload()).isInstanceOf(NotificationCreateRequestEventPayload.class);
        });

        var notificationPayload = (NotificationCreateRequestEventPayload) publishedEvents.get(0).payload();
        assertThat(notificationPayload.targetUserId()).isEqualTo(targetUserId);
        assertThat(notificationPayload.level()).isEqualTo(level);
        assertThat(notificationPayload.title()).isEqualTo(title);

        var payloadBody = objectMapper.readValue(notificationPayload.body(), WaitlistResult.class);
        assertThat(payloadBody.userId()).isEqualTo(targetUserId);
        assertThat(payloadBody.locator().roomId()).isEqualTo(request.getLocator().roomId());
        assertThat(payloadBody.locator().seatId()).isEqualTo(request.getLocator().seatId());
        assertThat(payloadBody.range().startAt()).isEqualTo(request.getRange().startAt());
        assertThat(payloadBody.range().endAt()).isEqualTo(request.getRange().endAt());
    }

    private void assertVacancyAlertRequestState(
            Waitlist request,
            WaitlistStatus status,
            WaitlistResolution resolution
    ) {
        var persisted = waitlistStore.findById(request.getId()).orElseThrow();
        assertThat(persisted.getState().getStatus()).isEqualTo(status);
        assertThat(persisted.getState().getResolution()).isEqualTo(resolution);
    }

    private void assertReservationCreated(String userId, SeatLocator locator, TimeRange range) {
        var reservation = reservationReader.findByUserId(userId).orElseThrow();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(reservation.getLocator().roomId()).isEqualTo(locator.roomId());
        assertThat(reservation.getLocator().seatId()).isEqualTo(locator.seatId());
        assertThat(reservation.getRange().startAt()).isEqualTo(range.startAt());
        assertThat(reservation.getRange().endAt()).isEqualTo(range.endAt());
    }

    private record SeatVacancyTarget(
            String reservationUserId,
            SeatLocator locator,
            TimeRange range
    ) {
    }
}
