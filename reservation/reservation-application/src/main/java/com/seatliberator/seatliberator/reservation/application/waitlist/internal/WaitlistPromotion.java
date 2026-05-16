package com.seatliberator.seatliberator.reservation.application.waitlist.internal;

import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitlistPromotion {
    private final ReservationCreator creator;

    public WaitlistPromotionResult promote(String userId, SeatLocator locator, InstantRange range) {
        try {
            creator.create(userId);
            return WaitlistPromotionResult.success();
        } catch (Exception exception) {
            return WaitlistPromotionResult.fail("예약 실패");
        }
    }
}
