package com.seatliberator.seatliberator.reservation.application.waitlist.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.waitlist.contract.WaitlistCancelAuthorizer;
import com.seatliberator.seatliberator.reservation.application.waitlist.contract.WaitlistOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.CancelWaitlistUseCase;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CancelWaitlistCommand;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistReader;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelWaitlistService implements CancelWaitlistUseCase {
    private final WaitlistReader reader;
    private final WaitlistStore store;

    private final WaitlistCancelAuthorizer authorizer;
    private final WaitlistOwnershipPolicy ownershipPolicy;
    private final ActorContextHolder actorContextHolder;
    private final Clock clock;

    @Override
    public void cancel(CancelWaitlistCommand command) {
        var actor = actorContextHolder.getActor();
        authorizer.validate(actor);

        var waitlist = reader.findById(command.waitlistId())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.WAITLIST_NOT_FOUND));
        ownershipPolicy.validate(waitlist, actor);

        var now = clock.instant();
        waitlist.cancel(now);
        store.save(waitlist);
    }
}
