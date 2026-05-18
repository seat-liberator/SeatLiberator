package com.seatliberator.seatliberator.reservation.application.occupancy.contract;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyDecision;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyReason;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SeatOccupancyPolicyReason implements PolicyReason {
    EMPTY_OCCUPANCIES(
            PolicyDecision.REJECTED,
            "seat-occupancy.rejected.empty-occupancies",
            "점유한 좌석 슬롯이 없습니다."
    ),
    DIFFERENT_OCCUPANCY_DATE_INCLUDED(
            PolicyDecision.REJECTED,
            "seat-occupancy.rejected.different-occupancy-date-included",
            "서로 다른 점유 날짜가 포함되어있습니다."
    );

    private final PolicyDecision decision;
    private final String code;
    private final String message;

    @Override
    public PolicyDecision decision() {
        return decision;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
