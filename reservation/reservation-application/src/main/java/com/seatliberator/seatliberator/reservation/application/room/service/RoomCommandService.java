package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.internal.RoomOperationPolicyProvisioner;
import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.DeleteRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.UpdateRoomCodeUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.DeleteRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomCodeCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomCommandService implements
        CreateRoomUseCase,
        UpdateRoomCodeUseCase,
        DeleteRoomUseCase {
    private final RoomReader reader;
    private final RoomStore store;

    private final RoomOperationPolicyProvisioner policyProvisioner;
    private final Clock clock;

    @Override
    public RoomResult create(CreateRoomCommand command) {
        var code = command.code();
        var existsRoom = reader.existsByCode(code);
        if (existsRoom) throw new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_ALREADY_EXISTS);

        var now = clock.instant();
        var operationPolicy = policyProvisioner.provide();
        var room = Room.of(code, operationPolicy, now);
        store.save(room);

        return RoomResult.from(room);
    }

    @Override
    public RoomResult update(UpdateRoomCodeCommand command) {
        var roomId = command.roomId();
        var room = reader.findById(roomId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_NOT_FOUND));

        var newCode = command.newCode();
        if (room.getCode().equals(command.newCode())) return RoomResult.from(room);

        var existsRoom = reader.existsByCode(newCode);
        if (existsRoom) throw new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_ALREADY_EXISTS);

        room.updateCode(newCode);
        store.save(room);

        return RoomResult.from(room);
    }

    @Override
    public void delete(DeleteRoomCommand command) {
        var roomId = command.roomId();
        var room = reader.findById(roomId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_NOT_FOUND));
        store.delete(room);
    }
}
