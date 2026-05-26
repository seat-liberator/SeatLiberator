package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.port.in.FindRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.ListRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.query.FindRoomQuery;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomQueryService implements
        ListRoomUseCase,
        FindRoomUseCase {
    private final RoomReader reader;

    @Override
    public RoomResult find(FindRoomQuery query) {
        var roomId = query.roomId();
        return reader.findById(roomId)
                .map(RoomResult::from)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_NOT_FOUND));
    }

    @Override
    public List<RoomResult> list() {
        return reader.findAll().stream()
                .map(RoomResult::from)
                .toList();
    }
}
