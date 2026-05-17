package com.seatliberator.seatliberator.reservation.application.shared.exception;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyReason;
import lombok.Getter;

@Getter
public class ReservationApplicationPolicyException extends ReservationApplicationException {
    private final PolicyReason reason;

    public ReservationApplicationPolicyException(PolicyReason reason) {
        super(ReservationApplicationErrorCode.RESERVATION_POLICY_REJECTED);
        this.reason = reason;
    }
}
