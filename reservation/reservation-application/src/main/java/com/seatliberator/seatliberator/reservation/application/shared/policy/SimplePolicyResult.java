package com.seatliberator.seatliberator.reservation.application.shared.policy;

public record SimplePolicyResult(
        boolean accepted,
        PolicyReason reason
) implements PolicyResult {
    public SimplePolicyResult {
        PolicyResult.validate(accepted, reason);
    }

    public static <T extends PolicyReason> SimplePolicyResult accept(T reason) {
        return new SimplePolicyResult(true, reason);
    }

    public static <T extends PolicyReason> SimplePolicyResult reject(T reason) {
        return new SimplePolicyResult(false, reason);
    }
}
