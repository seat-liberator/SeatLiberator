package com.seatliberator.seatliberator.reservation.application.shared.policy;

public interface PolicyReason {
    PolicyDecision decision();

    String code();

    String message();
}
