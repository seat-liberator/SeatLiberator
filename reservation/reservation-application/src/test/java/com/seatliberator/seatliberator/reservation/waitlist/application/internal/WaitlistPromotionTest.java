package com.seatliberator.seatliberator.reservation.waitlist.application.internal;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreatePolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatePolicyCommand;
import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatorCommand;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import com.seatliberator.seatliberator.reservation.application.waitlist.internal.WaitlistPromotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.domain.shared.SeatLocatorFixture.createLocator;
import static com.seatliberator.seatliberator.reservation.domain.shared.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Waitlist Promotion")
class WaitlistPromotionTest {

    @Mock
    ReservationCreatePolicy createPolicy;

    @Mock
    ReservationCreator creator;

    WaitlistPromotion promotion;

    @BeforeEach
    void setUp() {
        promotion = new WaitlistPromotion(createPolicy, creator);
    }

    @Test
    @DisplayName("예약 정책을 통과하지 못하면 실패 사유를 그대로 반환한다")
    void return_reject_reason_when_policy_check_fails() {
        var userId = "user-1";
        var locator = createLocator();
        var range = createRange();
        when(createPolicy.evaluate(ReservationCreatePolicyCommand.of(userId, locator, range)))
                .thenReturn(SimplePolicyResult.reject(ReservationPolicyReason.SEAT_ALREADY_TAKEN));

        var result = promotion.promote(userId, locator, range);

        assertThat(result.succeed()).isFalse();
        assertThat(result.failReason()).isEqualTo(ReservationPolicyReason.SEAT_ALREADY_TAKEN.message());
        verifyNoInteractions(creator);
    }

    @Test
    @DisplayName("예약 정책을 통과하면 같은 좌석과 시간으로 예약 생성을 시도한다")
    void create_reservation_with_same_locator_and_range_when_policy_check_passes() {
        var userId = "user-1";
        var locator = createLocator();
        var range = createRange();
        when(createPolicy.evaluate(ReservationCreatePolicyCommand.of(userId, locator, range)))
                .thenReturn(SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_CREATABLE));

        var result = promotion.promote(userId, locator, range);

        assertThat(result.succeed()).isTrue();

        var commandCaptor = ArgumentCaptor.forClass(ReservationCreatorCommand.class);
        verify(creator).create(commandCaptor.capture());
        assertThat(commandCaptor.getValue()).isEqualTo(ReservationCreatorCommand.of(userId, locator, range));
    }

    @Test
    @DisplayName("예약 생성 중 예외가 발생하면 일반 실패로 변환한다")
    void return_generic_failure_when_create_reservation_throws_exception() {
        var userId = "user-1";
        var locator = createLocator();
        var range = createRange();
        when(createPolicy.evaluate(ReservationCreatePolicyCommand.of(userId, locator, range)))
                .thenReturn(SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_CREATABLE));
        doThrow(new IllegalStateException("boom"))
                .when(creator)
                .create(ReservationCreatorCommand.of(userId, locator, range));

        var result = promotion.promote(userId, locator, range);

        assertThat(result.succeed()).isFalse();
        assertThat(result.failReason()).isEqualTo("예약 실패");
    }
}
