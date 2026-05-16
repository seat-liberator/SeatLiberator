package com.seatliberator.seatliberator.reservation.application.seat.contract;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyDecision;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyReason;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SeatTimeSlotPolicyReason implements PolicyReason {
    SLOT_BUNDLE_RESERVABLE(
            PolicyDecision.ACCEPTED,
            "seat-time-slot.accepted.slot-bundle-reservable",
            "예약 가능한 좌석 시간 슬롯 집합입니다."
    ),

    EMPTY_SLOT(
            PolicyDecision.REJECTED,
            "seat-time-slot.rejected.empty-slot-ids",
            "좌석 시간 슬롯이 비어 있습니다."
    ),
    NULL_SLOT_INCLUDED(
            PolicyDecision.REJECTED,
            "seat-time-slot.rejected.null-slot-id-included",
            "좌석 시간 슬롯 식별자에 null이 포함되어 있습니다."
    ),
    DUPLICATE_SLOT(
            PolicyDecision.REJECTED,
            "seat-time-slot.rejected.duplicate-slot-ids",
            "중복된 좌석 시간 슬롯이 포함되어 있습니다."
    ),
    SLOT_NOT_FOUND(
            PolicyDecision.REJECTED,
            "seat-time-slot.rejected.slot-not-found",
            "존재하지 않는 좌석 시간 슬롯이 포함되어 있습니다."
    ),
    INACTIVE_SLOT_INCLUDED(
            PolicyDecision.REJECTED,
            "seat-time-slot.rejected.inactive-slot-included",
            "비활성 좌석 시간 슬롯이 포함되어 있습니다."
    ),
    DIFFERENT_SEAT_INCLUDED(
            PolicyDecision.REJECTED,
            "seat-time-slot.rejected.different-seat-included",
            "서로 다른 좌석의 시간 슬롯이 포함되어 있습니다."
    ),
    DISCONTINUOUS_TIME_SLOTS(
            PolicyDecision.REJECTED,
            "seat-time-slot.rejected.discontinuous-time-slots",
            "좌석 시간 슬롯이 연속적이지 않습니다."
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
