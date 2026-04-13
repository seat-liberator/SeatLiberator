package com.seatliberator.seatliberator.reservation.vacancy.application.handler;

import com.seatliberator.seatliberator.reservation.domain.*;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationCanceled;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationExpired;
import com.seatliberator.seatliberator.reservation.domain.fixture.VacancyAlertRequestFixtureBuilder;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.shared.application.notifier.Notifier;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.result.VacancyAlertRequestResult;
import com.seatliberator.seatliberator.reservation.vacancy.application.internal.VacancyAlertRequestPromotionResult;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import com.seatliberator.seatliberator.reservation.vacancy.application.internal.VacancyAlertRequestPromotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Seat Vacancy Handler")
class SeatVacancyHandlerTest {

    @Mock
    VacancyAlertRequestStore store;

    @Mock
    VacancyAlertRequestPromotion promotion;

    @Mock
    Notifier notifier;

    @Mock
    Clock clock;

    SeatVacancyHandler handler;

    @BeforeEach
    void run() {
        handler = new SeatVacancyHandler(store, promotion, notifier, clock);
    }

    @Test
    @DisplayName("예약 취소로 빈자리 발생 시 NOTIFY_ONLY 요청은 전부 complete 및 notify 처리된다")
    void notify_only_requests_are_completed_and_notified_when_reservation_is_canceled() {
        var locator = createLocator();
        var range = createRange();
        var requestedAt = fixedClock.instant();
        var now = requestedAt.plusSeconds(60);
        var requests = createRequests(locator, range, VacancyAlertRequestBehavior.NOTIFY_ONLY, List.of(
                new RequestSpec("user-1", requestedAt),
                new RequestSpec("user-2", requestedAt.plusSeconds(1)),
                new RequestSpec("user-3", requestedAt.plusSeconds(2))
        ));

        when(clock.instant()).thenReturn(now);
        when(store.findByLocatorAndRangeAndStatus(locator, range, VacancyAlertRequestStatus.ACTIVE))
                .thenReturn(requests);

        handler.handle(new ReservationCanceled(locator, range, now));

        assertNotifyOnlyCompleted(requests, now);
        assertInfoNotifications(requests, "빈 자리가 발생했어요!");

        var storedRequestCaptor = ArgumentCaptor.forClass(VacancyAlertRequest.class);
        verify(store, times(requests.size())).save(storedRequestCaptor.capture());
        assertThat(storedRequestCaptor.getAllValues()).containsExactlyElementsOf(requests);
        verify(promotion, never()).promote(anyString(), any(SeatLocator.class), any(TimeRange.class));
    }

    @Test
    @DisplayName("예약 만료로 빈자리 발생 시에도 NOTIFY_ONLY 요청은 전부 complete 및 notify 처리된다")
    void notify_only_requests_are_completed_and_notified_when_reservation_is_expired() {
        var locator = createLocator();
        var range = createRange();
        var requestedAt = fixedClock.instant();
        var now = requestedAt.plusSeconds(60);
        var requests = createRequests(locator, range, VacancyAlertRequestBehavior.NOTIFY_ONLY, List.of(
                new RequestSpec("user-1", requestedAt),
                new RequestSpec("user-2", requestedAt.plusSeconds(1))
        ));

        when(clock.instant()).thenReturn(now);
        when(store.findByLocatorAndRangeAndStatus(locator, range, VacancyAlertRequestStatus.ACTIVE))
                .thenReturn(requests);

        handler.handle(new ReservationExpired(locator, range, now));

        assertNotifyOnlyCompleted(requests, now);
        assertInfoNotifications(requests, "빈 자리가 발생했어요!");
        verify(store, times(requests.size())).save(any(VacancyAlertRequest.class));
    }

    @Test
    @DisplayName("AUTO_CLAIM 요청은 requestedAt 오름차순으로 첫 성공 후보까지만 promotion을 시도한다")
    void auto_claim_requests_are_promoted_in_requested_at_order_until_first_success() {
        var locator = createLocator();
        var range = createRange();
        var baseAt = fixedClock.instant();
        var now = baseAt.plusSeconds(60);
        var lateRequest = autoClaimRequest("user-late", locator, range, baseAt.plusSeconds(3));
        var firstRequest = autoClaimRequest("user-first", locator, range, baseAt);
        var secondRequest = autoClaimRequest("user-second", locator, range, baseAt.plusSeconds(2));
        var requests = List.of(lateRequest, firstRequest, secondRequest);

        when(clock.instant()).thenReturn(now);
        when(store.findByLocatorAndRangeAndStatus(locator, range, VacancyAlertRequestStatus.ACTIVE))
                .thenReturn(requests);
        when(promotion.promote(anyString(), any(SeatLocator.class), any(TimeRange.class)))
                .thenReturn(VacancyAlertRequestPromotionResult.success());

        handler.handle(new ReservationCanceled(locator, range, now));

        assertThat(firstRequest.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.COMPLETED);
        assertThat(firstRequest.getState().getResolution()).isEqualTo(VacancyAlertRequestResolution.CLAIMED);
        assertThat(firstRequest.getState().getCompletedAt()).isEqualTo(now);
        assertThat(secondRequest.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.ACTIVE);
        assertThat(lateRequest.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.ACTIVE);

        assertPromotionAttemptedFor("user-first");
        assertSavedAll(firstRequest);
        verify(notifier, only()).consume(
                eq("user-first"),
                eq("INFO"),
                eq("빈 자리를 예약했어요!"),
                eq(VacancyAlertRequestResult.from(firstRequest))
        );
    }

    @Test
    @DisplayName("AUTO_CLAIM 첫 후보 실패 시 다음 후보를 promotion하고 처리된 요청만 저장한다")
    void auto_claim_promotes_next_request_when_first_candidate_fails() {
        var locator = createLocator();
        var range = createRange();
        var baseAt = fixedClock.instant();
        var now = baseAt.plusSeconds(60);
        var firstRequest = autoClaimRequest("user-first", locator, range, baseAt);
        var secondRequest = autoClaimRequest("user-second", locator, range, baseAt.plusSeconds(1));
        var thirdRequest = autoClaimRequest("user-third", locator, range, baseAt.plusSeconds(2));
        var requests = List.of(thirdRequest, firstRequest, secondRequest);

        when(clock.instant()).thenReturn(now);
        when(store.findByLocatorAndRangeAndStatus(locator, range, VacancyAlertRequestStatus.ACTIVE))
                .thenReturn(requests);
        when(promotion.promote(anyString(), any(SeatLocator.class), any(TimeRange.class)))
                .thenReturn(
                        VacancyAlertRequestPromotionResult.fail("예약 정책 위반"),
                        VacancyAlertRequestPromotionResult.success()
                );

        handler.handle(new ReservationCanceled(locator, range, now));

        assertThat(firstRequest.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.FAILED);
        assertThat(firstRequest.getState().getFailedAt()).isEqualTo(now);
        assertThat(secondRequest.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.COMPLETED);
        assertThat(secondRequest.getState().getResolution()).isEqualTo(VacancyAlertRequestResolution.CLAIMED);
        assertThat(secondRequest.getState().getCompletedAt()).isEqualTo(now);
        assertThat(thirdRequest.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.ACTIVE);

        assertPromotionAttempts("user-first", "user-second");
        assertSavedAll(firstRequest, secondRequest);
        verify(notifier).consume(
                eq("user-first"),
                eq("WARNING"),
                eq("빈 자리 예약에 실패했어요."),
                eq(VacancyAlertRequestResult.from(firstRequest))
        );
        verify(notifier).consume(
                eq("user-second"),
                eq("INFO"),
                eq("빈 자리를 예약했어요!"),
                eq(VacancyAlertRequestResult.from(secondRequest))
        );
    }

    @Test
    @DisplayName("AUTO_CLAIM 요청이 전부 실패하면 모든 후보를 failed 처리하고 저장한다")
    void auto_claim_marks_every_candidate_failed_when_every_promotion_fails() {
        var locator = createLocator();
        var range = createRange();
        var baseAt = fixedClock.instant();
        var now = baseAt.plusSeconds(60);
        var firstRequest = autoClaimRequest("user-first", locator, range, baseAt);
        var secondRequest = autoClaimRequest("user-second", locator, range, baseAt.plusSeconds(1));
        var requests = List.of(secondRequest, firstRequest);

        when(clock.instant()).thenReturn(now);
        when(store.findByLocatorAndRangeAndStatus(locator, range, VacancyAlertRequestStatus.ACTIVE))
                .thenReturn(requests);
        when(promotion.promote(anyString(), any(SeatLocator.class), any(TimeRange.class)))
                .thenReturn(
                        VacancyAlertRequestPromotionResult.fail("예약 정책 위반"),
                        VacancyAlertRequestPromotionResult.fail("예약 실패")
                );

        handler.handle(new ReservationCanceled(locator, range, now));

        assertThat(firstRequest.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.FAILED);
        assertThat(firstRequest.getState().getFailedAt()).isEqualTo(now);
        assertThat(secondRequest.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.FAILED);
        assertThat(secondRequest.getState().getFailedAt()).isEqualTo(now);

        assertPromotionAttempts("user-first", "user-second");
        assertSavedAll(firstRequest, secondRequest);
        verify(notifier, times(2)).consume(
                anyString(),
                eq("WARNING"),
                eq("빈 자리 예약에 실패했어요."),
                any(VacancyAlertRequestResult.class)
        );
        verify(store, never()).save(any(VacancyAlertRequest.class));
    }

    private List<VacancyAlertRequest> createRequests(
            SeatLocator locator,
            TimeRange range,
            VacancyAlertRequestBehavior behavior,
            List<RequestSpec> requestSpecs
    ) {
        var requestBuilder = new VacancyAlertRequestFixtureBuilder()
                .locator(locator)
                .range(range)
                .behavior(behavior);

        return requestSpecs.stream()
                .map(spec -> requestBuilder.copy()
                        .userId(spec.userId())
                        .requestedAt(spec.requestedAt())
                        .build()
                )
                .toList();
    }

    private VacancyAlertRequest autoClaimRequest(String userId, SeatLocator locator, TimeRange range, java.time.Instant requestedAt) {
        return new VacancyAlertRequestFixtureBuilder()
                .locator(locator)
                .range(range)
                .behavior(VacancyAlertRequestBehavior.AUTO_CLAIM)
                .userId(userId)
                .requestedAt(requestedAt)
                .build();
    }

    private void assertNotifyOnlyCompleted(List<VacancyAlertRequest> requests, java.time.Instant completedAt) {
        assertThat(requests).allSatisfy(r -> {
            var state = r.getState();
            assertThat(state.getStatus()).isEqualTo(VacancyAlertRequestStatus.COMPLETED);
            assertThat(state.getResolution()).isEqualTo(VacancyAlertRequestResolution.NOTIFIED);
            assertThat(state.getCompletedAt()).isEqualTo(completedAt);
        });
    }

    private void assertInfoNotifications(List<VacancyAlertRequest> requests, String title) {
        var notifierUserIdCaptor = ArgumentCaptor.forClass(String.class);
        var notifierPayloadCaptor = ArgumentCaptor.forClass(VacancyAlertRequestResult.class);

        verify(notifier, times(requests.size())).consume(
                notifierUserIdCaptor.capture(),
                eq("INFO"),
                eq(title),
                notifierPayloadCaptor.capture()
        );

        assertThat(notifierUserIdCaptor.getAllValues())
                .containsExactlyElementsOf(requests.stream().map(VacancyAlertRequest::getUserId).toList());

        assertThat(notifierPayloadCaptor.getAllValues())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(requests.stream().map(VacancyAlertRequestResult::from).toList());
    }

    private void assertPromotionAttemptedFor(String userId) {
        assertPromotionAttempts(userId);
    }

    private void assertPromotionAttempts(String... userIds) {
        var promotionUserIdCaptor = ArgumentCaptor.forClass(String.class);
        var promotionLocatorCaptor = ArgumentCaptor.forClass(SeatLocator.class);
        var promotionRangeCaptor = ArgumentCaptor.forClass(TimeRange.class);

        verify(promotion, times(userIds.length)).promote(
                promotionUserIdCaptor.capture(),
                promotionLocatorCaptor.capture(),
                promotionRangeCaptor.capture()
        );

        assertThat(promotionUserIdCaptor.getAllValues()).containsExactly(userIds);
    }

    @SuppressWarnings("unchecked")
    private void assertSavedAll(VacancyAlertRequest... requests) {
        var savedAllCaptor = ArgumentCaptor.forClass(Iterable.class);

        verify(store).saveAll(savedAllCaptor.capture());
        assertThat((Iterable<VacancyAlertRequest>) savedAllCaptor.getValue()).containsExactly(requests);
    }

    private record RequestSpec(String userId, java.time.Instant requestedAt) {
    }
}
