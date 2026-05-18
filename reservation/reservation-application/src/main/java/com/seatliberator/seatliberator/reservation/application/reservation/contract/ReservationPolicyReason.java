package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyDecision;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyReason;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReservationPolicyReason implements PolicyReason {

    USER_ELIGIBLE(
            PolicyDecision.ACCEPTED,
            "reservation.accepted.user-eligible",
            "예약 가능한 사용자입니다."
    ),
    SEAT_AVAILABLE(
            PolicyDecision.ACCEPTED,
            "reservation.accepted.seat-available",
            "예약 가능한 좌석입니다."
    ),
    RESERVATION_CREATABLE(
            PolicyDecision.ACCEPTED,
            "reservation.accepted.reservation-creatable",
            "예약 생성이 가능합니다."
    ),
    RESERVATION_OWNER(
            PolicyDecision.ACCEPTED,
            "reservation.accepted.reservation-owner",
            "예약 소유자입니다."
    ),
    RESERVATION_MANAGER(
            PolicyDecision.ACCEPTED,
            "reservation.accepted.reservation-manager",
            "예약 관리 권한이 있습니다."
    ),
    AUTHORIZED_RESERVATION_READ(
            PolicyDecision.ACCEPTED,
            "reservation.accepted.authorized-reservation-read",
            "예약을 조회할 권한이 있습니다."
    ),
    AUTHORIZED_RESERVATION_CREATE(
            PolicyDecision.ACCEPTED,
            "reservation.accepted.authorized-reservation-create",
            "예약을 생성할 권한이 있습니다."
    ),
    AUTHORIZED_RESERVATION_CANCEL(
            PolicyDecision.ACCEPTED,
            "reservation.accepted.authorized-reservation-cancel",
            "예약을 취소할 권한이 있습니다."
    ),

    USER_BLOCKED(
            PolicyDecision.REJECTED,
            "reservation.rejected.user-blocked",
            "사용이 제한된 사용자입니다."
    ),
    SEAT_ALREADY_TAKEN(
            PolicyDecision.REJECTED,
            "reservation.rejected.seat-already-taken",
            "이미 예약된 좌석입니다."
    ),
    UNAUTHORIZED_RESERVATION_ACCESS(
            PolicyDecision.REJECTED,
            "reservation.rejected.unauthorized-reservation-access",
            "해당 예약에 접근할 권한이 없습니다."
    ),
    UNAUTHORIZED_RESERVATION_CREATE(
            PolicyDecision.REJECTED,
            "reservation.rejected.unauthorized-reservation-create",
            "예약을 생성할 권한이 없습니다."
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