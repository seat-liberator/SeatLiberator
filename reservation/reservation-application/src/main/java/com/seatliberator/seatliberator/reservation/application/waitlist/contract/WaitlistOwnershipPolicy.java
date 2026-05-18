package com.seatliberator.seatliberator.reservation.application.waitlist.contract;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistReader;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WaitlistOwnershipPolicy {
    private final WaitlistReader reader;

    public void validate(UUID waitlistId, Actor actor) {
        Preconditions.requireNonNull(waitlistId, "waitlistId");

        var waitlist = reader.findById(waitlistId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.WAITLIST_NOT_FOUND));

        validate(waitlist, actor);
    }

    public void validate(Waitlist waitlist, Actor actor) {
        var result = evaluate(waitlist, actor);
        if (result.rejected())
            throw new ReservationApplicationPolicyException(result.reason());
    }

    public PolicyResult evaluate(Waitlist waitlist, Actor actor) {
        Preconditions.requireNonNull(waitlist, "waitlist");
        Preconditions.requireNonNull(actor, "actor");

        var capabilities = actor.capabilities();
        if (capabilities.contains(ReservationCapability.WAITLIST_MANAGE)) {
            return SimplePolicyResult.accept(WaitlistPolicyReason.WAITLIST_MANAGER);
        }

        if (waitlist.getUserId().equals(actor.subject())) {
            return SimplePolicyResult.accept(WaitlistPolicyReason.WAITLIST_OWNER);
        } else {
            return SimplePolicyResult.reject(WaitlistPolicyReason.UNAUTHORIZED_WAITLIST_ACCESS);
        }
    }
}
