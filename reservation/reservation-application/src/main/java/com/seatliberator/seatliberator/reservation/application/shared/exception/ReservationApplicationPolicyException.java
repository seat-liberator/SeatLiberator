package com.seatliberator.seatliberator.reservation.application.shared.exception;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyReason;
import lombok.Getter;

@Getter
public class ReservationApplicationPolicyException extends ReservationApplicationException {
    private final String reasonCode;
    private final String reasonMessage;

    public ReservationApplicationPolicyException(PolicyReason reason) {
        super(ReservationApplicationErrorCode.RESERVATION_POLICY_REJECTED);
        this.reasonCode = reason.code();
        this.reasonMessage = reason.message();
    }
}
