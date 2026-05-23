package com.seatliberator.seatliberator.reservation.application.waitlist.service;

import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.seat.contract.SeatTimeSlotBundlePolicy;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.waitlist.contract.WaitlistCreateAuthorizer;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.CreateWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CreateWaitlistCommand;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.result.WaitlistResult;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistReader;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter.WaitlistFilter;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateWaitlistService implements CreateWaitlistUseCase {
    private final WaitlistReader reader;
    private final WaitlistStore store;

    private final WaitlistCreateAuthorizer authorizer;
    private final SeatTimeSlotBundlePolicy slotBundlePolicy;
    private final ActorContextHolder actorContextHolder;
    private final Clock clock;

    @Override
    public WaitlistResult create(CreateWaitlistCommand command) {
        var actor = actorContextHolder.getActor();
        authorizer.validate(actor);

        var slotIds = command.seatTimeSlotIds();

        slotBundlePolicy.validate(slotIds);

        var userId = command.userId();
        var occupancyDate = command.occupancyDate();
        var behavior = command.behavior();
        var duplicateFilter = WaitlistFilter.empty()
                .userId(userId)
                .status(WaitlistStatus.ACTIVE)
                .occupancyDate(occupancyDate)
                .slotIds(Set.copyOf(slotIds));

        if (reader.existsByFilter(duplicateFilter)) {
            throw new ReservationApplicationException(ReservationApplicationErrorCode.DUPLICATED_REQUEST);
        }

        var now = clock.instant();
        var waitlist = Waitlist.of(userId, slotIds, occupancyDate, behavior, now);

        var saved = store.save(waitlist);
        return WaitlistResult.from(saved);
    }
}
