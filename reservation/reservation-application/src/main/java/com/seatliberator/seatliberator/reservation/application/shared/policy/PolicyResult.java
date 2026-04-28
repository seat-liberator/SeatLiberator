package com.seatliberator.seatliberator.reservation.application.shared.policy;

public interface PolicyResult {
    static void validate(boolean accepted, PolicyReason reason) {
        if (reason == null) {
            throw new IllegalArgumentException("policy reason must not be null.");
        }

        if (reason.code() == null || reason.code().isBlank()) {
            throw new IllegalArgumentException("policy reason code must not be null or blank.");
        }

        if (reason.message() == null || reason.message().isBlank()) {
            throw new IllegalArgumentException("policy reason message must not be null or blank.");
        }

        var expectedDecision = accepted
                ? PolicyDecision.ACCEPTED
                : PolicyDecision.REJECTED;

        if (reason.decision() != expectedDecision) {
            throw new IllegalArgumentException(
                    "policy reason decision must match policy result decision."
            );
        }
    }

    boolean accepted();

    PolicyReason reason();

    default boolean rejected() {
        return !accepted();
    }
}
