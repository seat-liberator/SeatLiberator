package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;

public interface RoomOperationPolicyProvisioner {
    RoomOperationPolicy provide();
}
