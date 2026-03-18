package com.seatliberator.seatliberator.verification.application.policy;

import com.seatliberator.seatliberator.verification.application.port.in.command.Requester;

public interface ReservationPolicyEngine {
    boolean canRead(Long reservationId, Requester requester);

    boolean canVerify(Long reservationId, Requester requester);
}
