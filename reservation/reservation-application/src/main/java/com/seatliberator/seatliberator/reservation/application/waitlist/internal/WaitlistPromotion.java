package com.seatliberator.seatliberator.reservation.application.waitlist.internal;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreatePolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatePolicyCommand;
import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatorCommand;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitlistPromotion {
    private final ReservationCreatePolicy createPolicy;
    private final ReservationCreator creator;

    public WaitlistPromotionResult promote(String userId, SeatLocator locator, InstantRange range) {
        var createPolicyResult = createPolicy.evaluate(ReservationCreatePolicyCommand.of(userId, locator, range));
        if (createPolicyResult.rejected())
            return WaitlistPromotionResult.fail(createPolicyResult.reason().message());

        try {
            creator.create(ReservationCreatorCommand.of(userId, locator, range));
            return WaitlistPromotionResult.success();
        } catch (Exception exception) {
            return WaitlistPromotionResult.fail("예약 실패");
        }
    }
}
