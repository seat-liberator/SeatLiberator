package com.seatliberator.seatliberator.reservation.application.room.contract;

import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyResult;
import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;

public interface RoomOperationReservationPolicy {
    RoomPolicyResult evaluate(SeatLocator locator, InstantRange range);
}
