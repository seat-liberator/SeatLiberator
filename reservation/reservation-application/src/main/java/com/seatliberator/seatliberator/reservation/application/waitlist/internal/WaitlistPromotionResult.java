package com.seatliberator.seatliberator.reservation.application.waitlist.internal;

import org.jspecify.annotations.Nullable;

public record WaitlistPromotionResult(
        boolean succeed,
        @Nullable String failReason
) {
    public WaitlistPromotionResult {
        if (succeed && failReason != null) {
            throw new IllegalArgumentException("fail reason must be null when succeed flag is true");
        }

        if (!succeed && failReason == null) {
            throw new IllegalArgumentException("fail reason must not be null when succeed flag is false");
        }
    }

    public static WaitlistPromotionResult success() {
        return new WaitlistPromotionResult(true, null);
    }

    public static WaitlistPromotionResult fail(String failReason) {
        return new WaitlistPromotionResult(false, failReason);
    }
}
