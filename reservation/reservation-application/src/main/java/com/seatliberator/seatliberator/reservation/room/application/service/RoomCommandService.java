package com.seatliberator.seatliberator.reservation.room.application.service;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;
import com.seatliberator.seatliberator.reservation.room.application.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.CreateRoomCommand;
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
        CreateRoomUseCase {
    private final RoomReader reader;
    private final RoomStore store;
    private final Clock clock;

    @Override
    public RoomResult create(CreateRoomCommand command) {
        var exists = reader.isExistsByRoomId(command.roomId());
        if (exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_ALREADY_EXISTS);

        var room = Room.of(command.roomId(), clock.instant());
        store.save(room);

        return RoomResult.from(room);
    }
}
