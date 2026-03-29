package com.seatliberator.seatliberator.reservation.verification.application.policy;

import com.seatliberator.seatliberator.reservation.verification.application.port.in.command.Requester;

public interface ReservationPolicyEngine {
    boolean canRead(Long reservationId, Requester requester);

    boolean canVerify(Long reservationId, Requester requester);
}
