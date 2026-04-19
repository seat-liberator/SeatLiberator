package com.seatliberator.seatliberator.reservation.waitlist.application.handler;

import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.WaitlistStatus;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationCanceled;
import com.seatliberator.seatliberator.reservation.domain.event.ReservationExpired;
import com.seatliberator.seatliberator.reservation.domain.fixture.WaitlistFixtureBuilder;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;
import com.seatliberator.seatliberator.reservation.shared.application.notifier.Notifier;
import com.seatliberator.seatliberator.reservation.waitlist.application.internal.WaitlistPromotion;
import com.seatliberator.seatliberator.reservation.waitlist.application.internal.WaitlistPromotionResult;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.out.WaitlistStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application.handler: SeatVacancyHandler")
class SeatVacancyHandlerTest {

    @Mock
    WaitlistStore store;

    @Mock
    WaitlistPromotion promotion;

    @Mock
    Notifier notifier;

    @Mock
    Clock clock;

    SeatVacancyHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SeatVacancyHandler(store, promotion, notifier, clock);
    }

    @Test
    @DisplayName("예약 취소 이벤트를 받으면 활성 요청을 조회해 처리 결과를 저장하고 알린다")
    void handle_canceled_event() {
        var locator = SimpleSeatLocator.of("room-1", "seat-1");
        var range = SimpleTimeRange.of(Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:10:00Z"));
        var requestedAt = Instant.parse("2025-12-31T23:59:00Z");
        var now = Instant.parse("2026-01-01T00:01:00Z");
        var request = new WaitlistFixtureBuilder()
                .locator(locator)
                .range(range)
                .requestedAt(requestedAt)
                .build();

        when(clock.instant()).thenReturn(now);
        when(store.findByLocatorAndRangeAndStatus(locator, range, WaitlistStatus.ACTIVE))
                .thenReturn(List.of(request));

        handler.handle(new ReservationCanceled(locator, range, now));

        verify(store).findByLocatorAndRangeAndStatus(locator, range, WaitlistStatus.ACTIVE);
        verify(store).saveAll(any());
        verify(notifier).consume(eq(request.getUserId()), eq("INFO"), eq("빈 자리가 발생했어요!"), any());
        verifyNoInteractions(promotion);
    }

    @Test
    @DisplayName("예약 만료 이벤트를 받으면 AUTO_CLAIM 요청을 승격 결과에 따라 처리한다")
    void handle_expired_event() {
        var locator = SimpleSeatLocator.of("room-1", "seat-1");
        var range = SimpleTimeRange.of(Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:10:00Z"));
        var requestedAt = Instant.parse("2025-12-31T23:59:00Z");
        var now = Instant.parse("2026-01-01T00:01:00Z");
        var request = Waitlist.autoClaim("user-1", locator, range, requestedAt);

        when(clock.instant()).thenReturn(now);
        when(store.findByLocatorAndRangeAndStatus(locator, range, WaitlistStatus.ACTIVE))
                .thenReturn(List.of(request));
        when(promotion.promote(anyString(), any(), any()))
                .thenReturn(WaitlistPromotionResult.success());

        handler.handle(new ReservationExpired(locator, range, now));

        verify(store).findByLocatorAndRangeAndStatus(locator, range, WaitlistStatus.ACTIVE);
        verify(promotion).promote(eq("user-1"), any(), any());

        var saveAllCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(store).saveAll(saveAllCaptor.capture());
        assertThat((Iterable<Waitlist>) saveAllCaptor.getValue()).containsExactly(request);

        verify(notifier).consume(eq("user-1"), eq("INFO"), eq("빈 자리를 예약했어요!"), any());
    }
}
