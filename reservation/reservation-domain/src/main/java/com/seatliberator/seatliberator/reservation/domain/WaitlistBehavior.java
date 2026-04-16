package com.seatliberator.seatliberator.reservation.domain;

public enum WaitlistBehavior {
    // 대기열 등록 없이 알림만 전송함
    NOTIFY_ONLY,

    // 대기열에 등록하고 좌석이 비면 자동 예약까지 시도함
    AUTO_CLAIM;

    public boolean isNotifyOnly() {
        return this == NOTIFY_ONLY;
    }

    public boolean isQueued() {
        return this == AUTO_CLAIM;
    }
}
