package com.seatliberator.seatliberator.reservation.room.application.port.out;

public interface RoomReader {
    boolean isExistsByRoomId(String roomId);
}
