package com.seatliberator.seatliberator.reservation.domain;

public enum VacancyAlertRequestBehavior {
    // 빈자리 알람만 전송함
    NOTIFY_ONLY,

    // 대기열 등록까지 함
    AUTO_CLAIM;

    public boolean isNotifyOnly() {
        return this == NOTIFY_ONLY;
    }

    public boolean isQueued() {
        return this == AUTO_CLAIM;
    }
}
