package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.port.in.UpdateRoomOperationPolicyUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomOperationPolicyCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomOperationPolicyResult;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomOperationPolicyCommandService implements UpdateRoomOperationPolicyUseCase {
    private final RoomReader reader;
    private final RoomStore store;

    @Override
    @Transactional
    public RoomOperationPolicyResult update(UpdateRoomOperationPolicyCommand command) {
        var room = reader.findByRoomId(command.roomId())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_NOT_FOUND));

        var operationPolicy = RoomOperationPolicy.of(
                command.maxReservationPerUser(),
                command.maxReservationDuration(),
                command.operationStatus(),
                command.operationTimeSegments()
        );

        room.updateOperationPolicy(operationPolicy);

        store.save(room);

        return RoomOperationPolicyResult.from(operationPolicy);
    }
}
