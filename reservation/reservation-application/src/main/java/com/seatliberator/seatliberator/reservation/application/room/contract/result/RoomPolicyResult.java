package com.seatliberator.seatliberator.reservation.application.room.contract.result;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;

public record RoomPolicyResult(
        boolean accepted,
        RoomPolicyReason reason
) implements PolicyResult {
    public RoomPolicyResult {
        PolicyResult.validate(accepted, reason);
    }

    public static RoomPolicyResult accept(RoomPolicyReason reason) {
        return new RoomPolicyResult(true, reason);
    }

    public static RoomPolicyResult reject(RoomPolicyReason reason) {
        return new RoomPolicyResult(false, reason);
    }
}
