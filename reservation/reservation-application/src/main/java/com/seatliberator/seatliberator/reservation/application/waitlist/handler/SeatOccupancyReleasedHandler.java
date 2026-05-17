package com.seatliberator.seatliberator.reservation.application.waitlist.handler;

import com.seatliberator.seatliberator.reservation.application.waitlist.internal.WaitlistClaimProcessor;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistReader;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter.WaitlistFilter;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter.WaitlistOrder;
import com.seatliberator.seatliberator.reservation.domain.reservation.event.SeatOccupancyReleased;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistBehavior;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatOccupancyReleasedHandler {
    private final WaitlistReader reader;
    private final WaitlistClaimProcessor processor;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SeatOccupancyReleased event) {
        var slotIds = event.slotIds();
        var occupancyDate = event.occupancyDate();

        var claimed = handleAutoClaims(slotIds, occupancyDate);

        if (!claimed) {
            handleNotify(slotIds, occupancyDate);
        }
    }

    private boolean handleAutoClaims(Set<UUID> slotIds, LocalDate occupancyDate) {
        var waitlists = findWaitlist(WaitlistBehavior.AUTO_CLAIM, slotIds, occupancyDate);

        for (var waitlist : waitlists) {
            var id = waitlist.getId();
            try {
                var result = processor.tryClaim(id);

                if (result.accepted()) {
                    return true;
                }
            } catch (DataAccessException | TransactionException ignored) {
            }
        }
        return false;
    }

    private void handleNotify(Set<UUID> slotIds, LocalDate occupancyDate) {
        var waitlists = findWaitlist(WaitlistBehavior.NOTIFY_ONLY, slotIds, occupancyDate);
    }

    private List<Waitlist> findWaitlist(WaitlistBehavior behavior, Set<UUID> slotIds, LocalDate occupancyDate) {
        var filter = WaitlistFilter.empty()
                .behavior(behavior)
                .status(WaitlistStatus.ACTIVE)
                .slotIds(slotIds)
                .occupancyDate(occupancyDate);
        return reader.findByFilter(filter, WaitlistOrder.fifo());
    }
}
