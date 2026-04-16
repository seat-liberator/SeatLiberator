package com.seatliberator.seatliberator.reservation.vacancy.application.model;

import com.seatliberator.seatliberator.reservation.domain.*;
import com.seatliberator.seatliberator.reservation.domain.fixture.VacancyAlertRequestFixtureBuilder;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;
import com.seatliberator.seatliberator.reservation.waitlist.application.internal.WaitlistPromotionResult;
import com.seatliberator.seatliberator.reservation.waitlist.application.model.WaitlistNotification;
import com.seatliberator.seatliberator.reservation.waitlist.application.model.WaitlistRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Application.model: VacancyAlertRequests")
class WaitlistRequestsTest {

    @Test
    @DisplayName("NOTIFY_ONLY 요청은 전부 완료 처리하고 알림 명령을 만든다")
    void complete_all_notify_only_requests() {
        var locator = createLocator();
        var range = createRange();
        var requestedAt = fixedClock.instant();
        var now = requestedAt.plusSeconds(60);
        var requests = createRequests(locator, range, WaitlistBehavior.NOTIFY_ONLY, List.of(
                new RequestSpec("user-1", requestedAt),
                new RequestSpec("user-2", requestedAt.plusSeconds(1))
        ));

        var result = WaitlistRequests.from(requests).process(now, request -> {
            throw new AssertionError("NOTIFY_ONLY processing must not promote reservations");
        });

        assertThat(result.requestsToSave()).containsExactlyElementsOf(requests);
        assertThat(result.notifications())
                .extracting(WaitlistNotification::title)
                .containsExactly("빈 자리가 발생했어요!", "빈 자리가 발생했어요!");
        assertThat(result.notifications())
                .extracting(WaitlistNotification::userId)
                .containsExactly("user-1", "user-2");
        assertThat(requests).allSatisfy(request -> {
            assertThat(request.getState().getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
            assertThat(request.getState().getResolution()).isEqualTo(WaitlistResolution.NOTIFIED);
            assertThat(request.getState().getCompletedAt()).isEqualTo(now);
        });
    }

    @Test
    @DisplayName("AUTO_CLAIM 요청은 requestedAt 오름차순으로 첫 성공 후보까지만 처리한다")
    void stop_after_first_success_in_requested_at_order() {
        var locator = createLocator();
        var range = createRange();
        var baseAt = fixedClock.instant();
        var now = baseAt.plusSeconds(60);
        var lateRequest = autoClaimRequest("user-late", locator, range, baseAt.plusSeconds(3));
        var firstRequest = autoClaimRequest("user-first", locator, range, baseAt);
        var secondRequest = autoClaimRequest("user-second", locator, range, baseAt.plusSeconds(2));
        var requests = List.of(lateRequest, firstRequest, secondRequest);
        var attemptedUsers = new ArrayList<String>();

        var result = WaitlistRequests.from(requests).process(now, request -> {
            attemptedUsers.add(request.getUserId());
            return WaitlistPromotionResult.success();
        });

        assertThat(attemptedUsers).containsExactly("user-first");
        assertThat(result.requestsToSave()).containsExactly(firstRequest);
        assertThat(result.notifications())
                .extracting(WaitlistNotification::title)
                .containsExactly("빈 자리를 예약했어요!");
        assertThat(firstRequest.getState().getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
        assertThat(firstRequest.getState().getResolution()).isEqualTo(WaitlistResolution.CLAIMED);
        assertThat(secondRequest.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);
        assertThat(lateRequest.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);
    }

    @Test
    @DisplayName("AUTO_CLAIM 첫 후보가 실패하면 다음 후보를 계속 시도한다")
    void continue_to_next_candidate_when_first_auto_claim_fails() {
        var locator = createLocator();
        var range = createRange();
        var baseAt = fixedClock.instant();
        var now = baseAt.plusSeconds(60);
        var firstRequest = autoClaimRequest("user-first", locator, range, baseAt);
        var secondRequest = autoClaimRequest("user-second", locator, range, baseAt.plusSeconds(1));
        var thirdRequest = autoClaimRequest("user-third", locator, range, baseAt.plusSeconds(2));
        var requests = List.of(thirdRequest, firstRequest, secondRequest);
        var attemptedUsers = new ArrayList<String>();

        var result = WaitlistRequests.from(requests).process(now, request -> {
            attemptedUsers.add(request.getUserId());
            return request.getUserId().equals("user-first")
                    ? WaitlistPromotionResult.fail("예약 정책 위반")
                    : WaitlistPromotionResult.success();
        });

        assertThat(attemptedUsers).containsExactly("user-first", "user-second");
        assertThat(result.requestsToSave()).containsExactly(firstRequest, secondRequest);
        assertThat(result.notifications())
                .extracting(WaitlistNotification::title)
                .containsExactly("빈 자리 예약에 실패했어요.", "빈 자리를 예약했어요!");
        assertThat(firstRequest.getState().getStatus()).isEqualTo(WaitlistStatus.FAILED);
        assertThat(secondRequest.getState().getStatus()).isEqualTo(WaitlistStatus.COMPLETED);
        assertThat(secondRequest.getState().getResolution()).isEqualTo(WaitlistResolution.CLAIMED);
        assertThat(thirdRequest.getState().getStatus()).isEqualTo(WaitlistStatus.ACTIVE);
    }

    @Test
    @DisplayName("혼합 요청은 AUTO_CLAIM 처리 후 NOTIFY_ONLY 알림도 함께 만든다")
    void process_auto_claim_and_notify_only_together() {
        var locator = createLocator();
        var range = createRange();
        var baseAt = fixedClock.instant();
        var now = baseAt.plusSeconds(60);
        var autoClaimRequest = autoClaimRequest("auto-user", locator, range, baseAt);
        var notifyOnlyRequest = notifyOnlyRequest("notify-user", locator, range, baseAt.plusSeconds(1));

        var result = WaitlistRequests.from(List.of(notifyOnlyRequest, autoClaimRequest))
                .process(now, request -> WaitlistPromotionResult.success());

        assertThat(result.requestsToSave()).containsExactly(autoClaimRequest, notifyOnlyRequest);
        assertThat(result.notifications())
                .extracting(WaitlistNotification::title)
                .containsExactly("빈 자리를 예약했어요!", "빈 자리가 발생했어요!");
        assertThat(autoClaimRequest.getState().getResolution()).isEqualTo(WaitlistResolution.CLAIMED);
        assertThat(notifyOnlyRequest.getState().getResolution()).isEqualTo(WaitlistResolution.NOTIFIED);
    }

    private List<Waitlist> createRequests(
            SeatLocator locator,
            TimeRange range,
            WaitlistBehavior behavior,
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

    private Waitlist autoClaimRequest(String userId, SeatLocator locator, TimeRange range, java.time.Instant requestedAt) {
        return new VacancyAlertRequestFixtureBuilder()
                .locator(locator)
                .range(range)
                .behavior(WaitlistBehavior.AUTO_CLAIM)
                .userId(userId)
                .requestedAt(requestedAt)
                .build();
    }

    private Waitlist notifyOnlyRequest(String userId, SeatLocator locator, TimeRange range, java.time.Instant requestedAt) {
        return new VacancyAlertRequestFixtureBuilder()
                .locator(locator)
                .range(range)
                .behavior(WaitlistBehavior.NOTIFY_ONLY)
                .userId(userId)
                .requestedAt(requestedAt)
                .build();
    }

    private record RequestSpec(String userId, java.time.Instant requestedAt) {
    }
}
