package com.seatliberator.seatliberator.reservation.verification.application.policy;

import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationOwnershipChecker;
import com.seatliberator.seatliberator.reservation.verification.application.port.in.command.ActorRequester;
import com.seatliberator.seatliberator.reservation.verification.application.port.in.command.Requester;
import com.seatliberator.seatliberator.reservation.verification.application.port.in.command.RequesterType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultReservationPolicyEngine implements ReservationPolicyEngine {
    private final ReservationOwnershipChecker reservationOwnershipChecker;

    @Override
    public boolean canRead(Long reservationId, Requester requester) {
        if (requester instanceof ActorRequester(RequesterType type, String actorId)) {
            // RequesterType 및 세부 정책 변경을 대비해서 현재 코드 스타일을 유지함
            if (type == RequesterType.ADMIN || type == RequesterType.SYSTEM) return true;
            return type == RequesterType.USER && reservationOwnershipChecker.hasOwnership(reservationId, actorId);
        }

        // Requester 구현체 추가를 대비해서 현재 코드 스타일 유지함
        return false;
    }

    @Override
    public boolean canVerify(Long reservationId, Requester requester) {
        if (requester instanceof ActorRequester(RequesterType type, String actorId)) {
            if (type == RequesterType.USER) return false;
            return type == RequesterType.ADMIN || type == RequesterType.SYSTEM;
        }

        return false;
    }
}
