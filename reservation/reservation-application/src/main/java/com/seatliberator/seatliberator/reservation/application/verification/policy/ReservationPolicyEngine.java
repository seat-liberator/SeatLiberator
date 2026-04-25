package com.seatliberator.seatliberator.reservation.application.verification.policy;

import com.seatliberator.seatliberator.reservation.application.verification.in.command.Requester;

public interface ReservationPolicyEngine {
    boolean canRead(Long reservationId, Requester requester);

    boolean canVerify(Long reservationId, Requester requester);
}
