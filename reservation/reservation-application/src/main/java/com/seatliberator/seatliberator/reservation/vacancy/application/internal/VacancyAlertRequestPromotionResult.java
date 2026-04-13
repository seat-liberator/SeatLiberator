package com.seatliberator.seatliberator.reservation.vacancy.application.internal;

import org.jspecify.annotations.Nullable;

public record VacancyAlertRequestPromotionResult(
        boolean succeed,
        @Nullable String failReason
) {
    public VacancyAlertRequestPromotionResult {
        if (succeed && failReason != null) {
            throw new IllegalArgumentException("fail reason must be null when succeed flag is true");
        }

        if (!succeed && failReason == null) {
            throw new IllegalArgumentException("fail reason must not be null when succeed flag is false");
        }
    }

    public static VacancyAlertRequestPromotionResult success() {
        return new VacancyAlertRequestPromotionResult(true, null);
    }

    public static VacancyAlertRequestPromotionResult fail(String failReason) {
        return new VacancyAlertRequestPromotionResult(false, failReason);
    }
}
