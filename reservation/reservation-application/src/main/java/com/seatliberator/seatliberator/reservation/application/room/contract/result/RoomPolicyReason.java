package com.seatliberator.seatliberator.reservation.application.room.contract.result;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyDecision;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyReason;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoomPolicyReason implements PolicyReason {

    ROOM_OPERATION_AVAILABLE(
            PolicyDecision.ACCEPTED,
            "room.accepted.operation-available",
            "방 운영 정책상 예약 가능한 시간입니다."
    ),

    ROOM_NOT_FOUND(
            PolicyDecision.REJECTED,
            "room.rejected.room-not-found",
            "방을 찾을 수 없습니다."
    ),
    ROOM_OPERATION_CLOSED(
            PolicyDecision.REJECTED,
            "room.rejected.operation-closed",
            "운영 중인 방이 아닙니다."
    ),
    OUT_OF_OPERATION_HOURS(
            PolicyDecision.REJECTED,
            "room.rejected.out-of-operation-hours",
            "방 운영 시간이 아닙니다."
    ),
    MAX_RESERVATION_DURATION_EXCEEDED(
            PolicyDecision.REJECTED,
            "room.rejected.max-reservation-duration-exceeded",
            "방의 최대 예약 가능 시간을 초과했습니다."
    ),
    MAX_RESERVATION_PER_USER_EXCEEDED(
            PolicyDecision.REJECTED,
            "room.rejected.max-reservation-per-user-exceeded",
            "사용자별 최대 예약 가능 횟수를 초과했습니다."
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
