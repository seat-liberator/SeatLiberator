package com.seatliberator.seatliberator.reservation.application.waitlist.internal;

import com.seatliberator.seatliberator.reservation.application.occupancy.contract.SeatOccupancyAllocator;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationCreator;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import com.seatliberator.seatliberator.reservation.application.waitlist.contract.WaitlistPolicyReason;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistReader;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WaitlistClaimProcessor {
    private final WaitlistReader reader;
    private final WaitlistStore store;

    private final ReservationCreator reservationCreator;
    private final SeatOccupancyAllocator occupancyCreator;
    private final Clock clock;

    @Transactional
    public PolicyResult tryClaim(UUID waitlistId) {
        try {
            var waitlist = reader.findById(waitlistId)
                    .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.WAITLIST_NOT_FOUND));

            if (!waitlist.getState().getStatus().isActive()) {
                return SimplePolicyResult.reject(WaitlistPolicyReason.AUTO_CLAIM_FAILED);
            }

            var now = clock.instant();

            var reservation = reservationCreator.create(waitlist.getUserId());
            occupancyCreator.allocate(reservation.getId(), waitlist.getSlotIds(), waitlist.getOccupancyDate());

            waitlist.complete(now);
            store.save(waitlist);
            return SimplePolicyResult.accept(WaitlistPolicyReason.CLAIMABLE_WAITLIST);
        } catch (ReservationApplicationPolicyException e) {
            return SimplePolicyResult.reject(e.getReason());
        } catch (ReservationApplicationException | DataAccessException | IllegalStateException e) {
            return SimplePolicyResult.reject(WaitlistPolicyReason.AUTO_CLAIM_FAILED);
        }
    }
}
