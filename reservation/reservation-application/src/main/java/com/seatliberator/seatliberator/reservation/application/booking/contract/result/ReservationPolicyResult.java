package com.seatliberator.seatliberator.reservation.application.booking.contract.result;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;

public record ReservationPolicyResult(
        boolean accepted,
        ReservationPolicyReason reason
) implements PolicyResult {
    public ReservationPolicyResult {
        PolicyResult.validate(accepted, reason);
    }

    public static ReservationPolicyResult accept(ReservationPolicyReason reason) {
        return new ReservationPolicyResult(true, reason);
    }

    public static ReservationPolicyResult reject(ReservationPolicyReason reason) {
        return new ReservationPolicyResult(false, reason);
    }
}
