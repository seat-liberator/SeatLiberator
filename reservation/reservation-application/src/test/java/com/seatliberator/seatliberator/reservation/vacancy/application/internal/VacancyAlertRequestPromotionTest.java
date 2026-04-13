package com.seatliberator.seatliberator.reservation.vacancy.application.internal;

import com.seatliberator.seatliberator.reservation.book.application.contract.ReservationPolicyChecker;
import com.seatliberator.seatliberator.reservation.book.application.contract.result.ReservationPolicyCheckResult;
import com.seatliberator.seatliberator.reservation.book.application.contract.result.ReservationRejectReason;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Vacancy Alert Request Promotion")
class VacancyAlertRequestPromotionTest {

    @Mock
    ReservationPolicyChecker policyChecker;

    @Mock
    CreateReservationUseCase createReservationUseCase;

    VacancyAlertRequestPromotion promotion;

    @BeforeEach
    void setUp() {
        promotion = new VacancyAlertRequestPromotion(policyChecker, createReservationUseCase);
    }

    @Test
    @DisplayName("예약 정책을 통과하지 못하면 실패 사유를 그대로 반환한다")
    void return_reject_reason_when_policy_check_fails() {
        var userId = "user-1";
        var locator = createLocator();
        var range = createRange();
        when(policyChecker.check(userId, locator, range))
                .thenReturn(ReservationPolicyCheckResult.reject(ReservationRejectReason.SEAT_ALREADY_TAKEN));

        var result = promotion.promote(userId, locator, range);

        assertThat(result.succeed()).isFalse();
        assertThat(result.failReason()).isEqualTo("이미 예약된 좌석");
        verifyNoInteractions(createReservationUseCase);
    }

    @Test
    @DisplayName("예약 정책을 통과하면 같은 좌석과 시간으로 예약 생성을 시도한다")
    void create_reservation_with_same_locator_and_range_when_policy_check_passes() {
        var userId = "user-1";
        var locator = createLocator();
        var range = createRange();
        when(policyChecker.check(userId, locator, range))
                .thenReturn(ReservationPolicyCheckResult.accept());

        var result = promotion.promote(userId, locator, range);

        assertThat(result.succeed()).isTrue();

        var commandCaptor = ArgumentCaptor.forClass(CreateReservationCommand.class);
        verify(createReservationUseCase).create(commandCaptor.capture());
        assertThat(commandCaptor.getValue()).isEqualTo(CreateReservationCommand.of(userId, locator, range));
    }

    @Test
    @DisplayName("예약 생성 중 예외가 발생하면 일반 실패로 변환한다")
    void return_generic_failure_when_create_reservation_throws_exception() {
        var userId = "user-1";
        var locator = createLocator();
        var range = createRange();
        when(policyChecker.check(userId, locator, range))
                .thenReturn(ReservationPolicyCheckResult.accept());
        doThrow(new IllegalStateException("boom"))
                .when(createReservationUseCase)
                .create(CreateReservationCommand.of(userId, locator, range));

        var result = promotion.promote(userId, locator, range);

        assertThat(result.succeed()).isFalse();
        assertThat(result.failReason()).isEqualTo("예약 실패");
    }
}
