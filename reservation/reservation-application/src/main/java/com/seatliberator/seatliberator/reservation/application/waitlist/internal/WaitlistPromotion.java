package com.seatliberator.seatliberator.reservation.application.waitlist.internal;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationPolicyChecker;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationRejectReason;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaitlistPromotion {
    private final ReservationPolicyChecker policyChecker;
    private final CreateReservationUseCase createReservationUseCase;

    public WaitlistPromotionResult promote(String userId, SeatLocator locator, TimeRange range) {
        var policyCheckResult = policyChecker.check(userId, locator, range);

        if (!policyCheckResult.reservable()) {
            var failReason = Optional.ofNullable(policyCheckResult.rejectReason()).map(ReservationRejectReason::message).orElse("예약 정책 위반");
            return WaitlistPromotionResult.fail(failReason);
        }

        try {
            var command = CreateReservationCommand.of(userId, locator, range);
            createReservationUseCase.create(command);
            return WaitlistPromotionResult.success();
        } catch (Exception exception) {
            return WaitlistPromotionResult.fail("예약 실패");
        }
    }
}
