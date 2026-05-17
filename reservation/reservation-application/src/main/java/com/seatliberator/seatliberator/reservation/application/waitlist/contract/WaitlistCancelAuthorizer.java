package com.seatliberator.seatliberator.reservation.application.waitlist.contract;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitlistCancelAuthorizer {
    public void validate(Actor actor) {
        var result = evaluate(actor);
        if (result.rejected())
            throw new ReservationApplicationPolicyException(result.reason());
    }

    public PolicyResult evaluate(Actor actor) {
        Preconditions.requireNonNull(actor, "actor");
        var capabilities = actor.capabilities();

        if (capabilities.contains(ReservationCapability.WAITLIST_MANAGE))
            return SimplePolicyResult.accept(WaitlistPolicyReason.WAITLIST_MANAGER);

        if (capabilities.contains(ReservationCapability.WAITLIST_CANCEL))
            return SimplePolicyResult.accept(WaitlistPolicyReason.AUTHORIZED_WAITLIST_CANCEL);

        return SimplePolicyResult.reject(WaitlistPolicyReason.UNAUTHORIZED_WAITLIST_CANCEL);
    }
}
