package com.seatliberator.seatliberator.reservation.domain.validator;

import com.seatliberator.seatliberator.reservation.domain.WaitlistResolution;
import com.seatliberator.seatliberator.reservation.domain.WaitlistStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.WaitlistState;

public class WaitlistStateValidator {
    public static void ensureStateIn(WaitlistState state, WaitlistStatus... allowed) {
        for (var status : allowed) if (state.getStatus() == status) return;

        switch (state.getStatus()) {
            case ACTIVE -> throw new IllegalStateException("이미 활성화된 대기열입니다.");
            case CANCELLED -> throw new IllegalStateException("이미 취소된 대기열입니다.");
            case EXPIRED -> throw new IllegalStateException("이미 만료된 대기열입니다.");
            case COMPLETED -> throw new IllegalStateException("이미 완료된 대기열입니다.");
            case FAILED -> throw new IllegalStateException("이미 실패한 대기열입니다.");
        }
    }

    public static void validate(WaitlistState state) {
        switch (state.getStatus()) {
            case ACTIVE -> validateActiveStatus(state);
            case CANCELLED -> validateCancelledStatus(state);
            case EXPIRED -> validateExpiredStatus(state);
            case COMPLETED -> validateCompleteStatus(state);
            case FAILED -> validateFailStatus(state);
        }
    }

    private static void validateActiveStatus(WaitlistState state) {
        if (state.getResolution() != WaitlistResolution.PENDING)
            throw new IllegalStateException("Active request must have PENDING resolution");
        if (state.getCancelledAt() != null || state.getExpiredAt() != null || state.getFailedAt() != null || state.getCompletedAt() != null)
            throw new IllegalStateException("Active request cannot have terminal timestamp");
    }

    private static void validateCancelledStatus(WaitlistState state) {
        if (state.getResolution() != WaitlistResolution.PENDING)
            throw new IllegalStateException("Cancelled request must have PENDING resolution");
        var canceledAt = state.getCancelledAt();
        if (canceledAt == null) throw new IllegalStateException("Cancelled request must have cancelledAt");
        if (state.getExpiredAt() != null || state.getFailedAt() != null || state.getCompletedAt() != null)
            throw new IllegalStateException("Cancelled request cannot have expiredAt or failedAt or completedAt");
        if (canceledAt.isBefore(state.getRequestedAt()))
            throw new IllegalStateException("cancelledAt must not be before requestedAt");
    }

    private static void validateExpiredStatus(WaitlistState state) {
        if (state.getResolution() != WaitlistResolution.PENDING)
            throw new IllegalStateException("Expired request must have PENDING resolution");
        var expiredAt = state.getExpiredAt();
        if (expiredAt == null) throw new IllegalStateException("Expired request must have expiredAt");
        if (state.getCancelledAt() != null || state.getFailedAt() != null || state.getCompletedAt() != null)
            throw new IllegalStateException("Expired request cannot have cancelledAt or failedAt or completedAt");
        if (expiredAt.isBefore(state.getRequestedAt()))
            throw new IllegalStateException("expiredAt must not be before requestedAt");
    }

    private static void validateFailStatus(WaitlistState state) {
        if (state.getResolution() != WaitlistResolution.PENDING)
            throw new IllegalStateException("Failed request must have PENDING resolution");
        var failedAt = state.getFailedAt();
        if (failedAt == null) throw new IllegalStateException("Failed request must have failedAt");
        if (state.getCancelledAt() != null || state.getExpiredAt() != null || state.getCompletedAt() != null)
            throw new IllegalStateException("Failed request cannot have cancelledAt or expiredAt or completedAt");
        if (failedAt.isBefore(state.getRequestedAt()))
            throw new IllegalStateException("failedAt must not be before requestedAt");
    }

    private static void validateCompleteStatus(WaitlistState state) {
        if (state.getResolution() == WaitlistResolution.PENDING)
            throw new IllegalStateException("Completed request must have a final resolution");
        var completedAt = state.getCompletedAt();
        if (completedAt == null) throw new IllegalStateException("Completed request must have completedAt");
        if (state.getCancelledAt() != null || state.getExpiredAt() != null)
            throw new IllegalStateException("Completed request cannot have cancelledAt or expiredAt");
        if (completedAt.isBefore(state.getRequestedAt()))
            throw new IllegalStateException("completedAt must not be before requestedAt");
    }
}
