package com.seatliberator.seatliberator.reservation.application.room.port.in;

import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomOperationPolicyCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomOperationPolicyResult;

public interface UpdateRoomOperationPolicyUseCase {
    RoomOperationPolicyResult update(UpdateRoomOperationPolicyCommand command);
}
