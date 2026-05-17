package com.seatliberator.seatliberator.reservation.application.waitlist.contract;

import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyDecision;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyReason;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WaitlistPolicyReason implements PolicyReason {
    CLAIMABLE_WAITLIST(
            PolicyDecision.ACCEPTED,
            "waitlist.accepted.claimable-waitlist",
            "점유 가능한 대기열입니다."
    ),
    WAITLIST_MANAGER(
            PolicyDecision.ACCEPTED,
            "waitlist.accepted.waitlist-manager",
            "대기열 관리 권한이 있습니다."
    ),
    WAITLIST_OWNER(
            PolicyDecision.ACCEPTED,
            "waitlist.accepted.waitlist-owner",
            "대기열 소유자입니다."
    ),
    AUTHORIZED_WAITLIST_CREATE(
            PolicyDecision.ACCEPTED,
            "waitlist.accepted.authorized-waitlist-create",
            "대기열 생성 권한이 있습니다."
    ),
    AUTHORIZED_WAITLIST_CANCEL(
            PolicyDecision.ACCEPTED,
            "waitlist.accepted.authorized-waitlist-cancel",
            "대기열 취소 권한이 있습니다."
    ),

    UNAUTHORIZED_WAITLIST_ACCESS(
            PolicyDecision.REJECTED,
            "waitlist.rejected.unauthorized-waitlist-access",
            "해당 대기열에 접근할 권한이 없습니다."
    ),
    UNAUTHORIZED_WAITLIST_CREATE(
            PolicyDecision.REJECTED,
            "waitlist.rejected.unauthorized-waitlist-create",
            "대기열 생성 권한이 없습니다."
    ),
    UNAUTHORIZED_WAITLIST_CANCEL(
            PolicyDecision.REJECTED,
            "waitlist.rejected.unauthorized-waitlist-cancel",
            "대기열 취소 권한이 없습니다."
    ),
    AUTO_CLAIM_FAILED(
            PolicyDecision.REJECTED,
            "waitlist.rejected.auto-caim-failed",
            "대기열이 자동 점유에 실패했습니다."
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
