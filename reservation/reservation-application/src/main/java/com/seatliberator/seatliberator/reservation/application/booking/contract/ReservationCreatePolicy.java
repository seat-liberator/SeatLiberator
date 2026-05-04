package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatePolicyCommand;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;

public interface ReservationCreatePolicy {
    SimplePolicyResult evaluate(ReservationCreatePolicyCommand command);
}
