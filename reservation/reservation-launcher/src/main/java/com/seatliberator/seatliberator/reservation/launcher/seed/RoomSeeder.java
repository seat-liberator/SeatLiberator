package com.seatliberator.seatliberator.reservation.launcher.seed;

import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.CreateSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.CreateSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatTimeSlotCommand;
import com.seatliberator.seatliberator.starter.launcher.seed.Seeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomSeeder implements Seeder {
    private final CreateRoomUseCase createRoomUseCase;
    private final CreateSeatUseCase createSeatUseCase;
    private final CreateSeatTimeSlotUseCase createSeatTimeSlotUseCase;
    private final ReservationApplicationSeedProperties properties;

    @Override
    public void seed() {
        var roomProperties = properties.room();
        var seatProperties = properties.seat();
        var seatTimeSlotProperties = properties.seatTimeSlot();

        for (int roomNum = 1; roomNum <= roomProperties.num(); roomNum++) {
            var roomCode = String.format(roomProperties.codePrefixFormat(), roomNum);
            var roomCommand = CreateRoomCommand.of(roomCode);
            var room = createRoomUseCase.create(roomCommand);
            log.info("Room created. id=%s".formatted(room.roomId()));

            for (int seatNum = 1; seatNum <= seatProperties.num(); seatNum++) {
                var seatCode = String.format(seatProperties.codePrefixFormat(), seatNum);
                var seatCommand = CreateSeatCommand.of(room.roomId(), seatCode);
                var seat = createSeatUseCase.create(seatCommand);

                seatTimeSlotProperties.schedule().forEach(time -> {
                    var slotCommand = CreateSeatTimeSlotCommand.of(seat.seatId(), time, seatTimeSlotProperties.duration());
                    var slot = createSeatTimeSlotUseCase.create(slotCommand);
                });
            }
        }
    }
}
