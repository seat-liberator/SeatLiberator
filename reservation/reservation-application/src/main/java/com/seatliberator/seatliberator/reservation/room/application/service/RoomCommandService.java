package com.seatliberator.seatliberator.reservation.room.application.service;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;
import com.seatliberator.seatliberator.reservation.room.application.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.DeleteRoomUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.UpdateRoomUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.DeleteRoomCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.UpdateRoomCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.RoomResult;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomCommandService implements
        CreateRoomUseCase,
        UpdateRoomUseCase,
        DeleteRoomUseCase {
    private final RoomReader reader;
    private final RoomStore store;
    private final Clock clock;

    @Override
    public RoomResult create(CreateRoomCommand command) {
        ensureRoomNotExists(command.roomId());

        var room = Room.of(command.roomId(), clock.instant());
        store.save(room);

        return RoomResult.from(room);
    }

    @Override
    public RoomResult update(UpdateRoomCommand command) {
        var room = tryFindByRoomId(command.oldRoomId());
        if (command.oldRoomId().equals(command.newRoomId())) return RoomResult.from(room);

        ensureRoomNotExists(command.newRoomId());

        room.updateRoomId(command.newRoomId());
        store.save(room);

        return RoomResult.from(room);
    }

    @Override
    public void delete(DeleteRoomCommand command) {
        var exists = reader.existsByRoomId(command.roomId());
        if (!exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_NOT_FOUND);
        store.deleteByRoomId(command.roomId());
    }

    private Room tryFindByRoomId(String roomId) {
        return reader.findByRoomId(roomId).orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_NOT_FOUND));
    }

    private void ensureRoomNotExists(String roomId) {
        var exists = reader.existsByRoomId(roomId);
        if (exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_ALREADY_EXISTS);
    }
}
