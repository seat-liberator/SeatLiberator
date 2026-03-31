package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.notification.api.event.NotificationCreateRequestEventPayload;
import com.seatliberator.seatliberator.notification.api.event.NotificationEventType;
import com.seatliberator.seatliberator.reservation.api.event.VacancyAlertNotificationPayload;
import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationManager;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.service.SeatService;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequester;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertCancelCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@TransactionalReservationIntegrationTest
@DisplayName("Integration: Reservation Cancel Vacancy Alert Flow")
public class ReservationCancelVacancyAlertFlowTest extends ReservationDatabaseCleanupSupport {
    private static final Instant BASE_TIME = Instant.parse("2026-01-01T00:00:00Z");

    @Autowired
    SeatService seatService;

    @Autowired
    ReservationManager reservationManager;

    @Autowired
    VacancyAlertRequester vacancyAlertRequester;

    @Autowired
    VacancyAlertRequestReader vacancyAlertRequestReader;

    @Autowired
    ReservationIntegrationTestConfiguration.TestEventPublisher testEventPublisher;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void clearPublishedEvents() {
        testEventPublisher.clear();
    }

    @Test
    @DisplayName("예약 취소 시 유효한 빈자리 알림 요청에만 알림 생성 흐름이 연결된다")
    void publish_notification_and_fulfill_only_active_matching_request_when_reservation_is_canceled() throws Exception {
        // 취소 대상 예약이 속한 좌석과, 비교용으로 섞어둘 다른 좌석
        var roomId = "room-1";
        var seatId = "seat-1";
        var otherSeatId = "seat-2";

        // reservationUserId: 실제 예약을 만든 사용자
        // activeUserId: 취소 후 알림을 받아야 하는 활성 요청 사용자
        // canceledUserId: 미리 알림 요청을 취소해 둔 사용자
        // otherSeatUserId: 다른 좌석에 알림을 걸어둔 사용자
        var reservationUserId = "reservation-user";
        var activeUserId = "active-user";
        var canceledUserId = "canceled-user";
        var otherSeatUserId = "other-seat-user";

        // 취소될 예약의 원래 시간 범위
        var startAt = BASE_TIME.plusSeconds(60);
        var endAt = BASE_TIME.plusSeconds(120);

        // 예약 대상 좌석과 비교용 좌석을 미리 생성한다.
        seatService.create(new SeatCreateCommand(roomId, seatId));
        seatService.create(new SeatCreateCommand(roomId, otherSeatId));

        // 이후 cancel 대상이 될 예약을 하나 만든다.
        reservationManager.create(new ReservationCreateCommand(
                reservationUserId,
                roomId,
                seatId,
                startAt,
                endAt
        ));

        // 같은 좌석/시간대를 기다리는 활성 요청: 취소 후 알림 대상이 되어야 한다.
        var activeRequest = vacancyAlertRequester.request(new VacancyAlertRequestCommand(
                activeUserId,
                roomId,
                seatId,
                startAt.plusSeconds(10),
                endAt.minusSeconds(10)
        ));

        // 같은 좌석/시간대 요청이지만 미리 취소해 둔다: 대상에서 제외되어야 한다.
        var canceledRequest = vacancyAlertRequester.request(new VacancyAlertRequestCommand(
                canceledUserId,
                roomId,
                seatId,
                startAt.plusSeconds(15),
                endAt.minusSeconds(15)
        ));

        // 다른 좌석 요청: 시간대가 겹쳐도 대상이 아니어야 한다.
        var otherSeatRequest = vacancyAlertRequester.request(new VacancyAlertRequestCommand(
                otherSeatUserId,
                roomId,
                otherSeatId,
                startAt.plusSeconds(10),
                endAt.minusSeconds(10)
        ));
        vacancyAlertRequester.cancelVacancyAlert(new VacancyAlertCancelCommand(canceledUserId, canceledRequest.getId()));

        // 예약 취소가 도메인 이벤트를 만들고, handler가 알림 생성 흐름을 이어야 한다.
        reservationManager.cancel(reservationUserId);

        // 실제 외부 전송 대신 테스트용 publisher에 쌓인 발행 이벤트를 검증한다.
        var publishedEvents = testEventPublisher.publishedEvents();
        assertThat(publishedEvents).singleElement().satisfies(event -> {
            assertThat(event.type()).isEqualTo(NotificationEventType.NOTIFICATION_CREATE_REQUEST);
            assertThat(event.payload()).isInstanceOf(NotificationCreateRequestEventPayload.class);
        });

        // 발행된 notification payload의 수신자와 본문이 활성 요청 기준으로 만들어졌는지 확인한다.
        var notificationPayload = (NotificationCreateRequestEventPayload) publishedEvents.get(0).payload();
        assertThat(notificationPayload.targetUserId()).isEqualTo(activeUserId);
        assertThat(notificationPayload.level()).isEqualTo("INFO");
        assertThat(notificationPayload.title()).isEqualTo("빈자리 발생!");

        var payloadBody = objectMapper.readValue(notificationPayload.body(), VacancyAlertNotificationPayload.class);
        assertThat(payloadBody.roomId()).isEqualTo(roomId);
        assertThat(payloadBody.seatId()).isEqualTo(seatId);
        assertThat(payloadBody.startTime()).isEqualTo(activeRequest.getRange().startAt());
        assertThat(payloadBody.endTime()).isEqualTo(activeRequest.getRange().endAt());

        // 활성 요청만 FULFILLED 로 전이되고, 취소된 요청과 다른 좌석 요청은 그대로 남아야 한다.
        var fulfilledRequest = vacancyAlertRequestReader.findById(activeRequest.getId()).orElseThrow();
        var persistedCanceledRequest = vacancyAlertRequestReader.findById(canceledRequest.getId()).orElseThrow();
        var persistedOtherSeatRequest = vacancyAlertRequestReader.findById(otherSeatRequest.getId()).orElseThrow();

        assertThat(fulfilledRequest.getStatus()).isEqualTo(VacancyAlertStatus.FULFILLED);
        assertThat(persistedCanceledRequest.getStatus()).isEqualTo(VacancyAlertStatus.CANCELLED);
        assertThat(persistedOtherSeatRequest.getStatus()).isEqualTo(VacancyAlertStatus.ACTIVE);
    }
}
