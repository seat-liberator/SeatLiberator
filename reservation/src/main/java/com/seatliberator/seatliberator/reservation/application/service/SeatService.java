package com.seatliberator.seatliberator.reservation.application.service;

import com.seatliberator.seatliberator.reservation.application.port.in.SeatManager;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatUpdateCommand;
import com.seatliberator.seatliberator.reservation.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.domain.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService implements SeatManager {

    private final SeatStore seatStore;

    @Override
    public boolean create(SeatCreateCommand command){

        if(seatStore.existsSeatConflict(
                command.roomId(),
                command.seatId()
        )){
            return false;
        }

        Seat seat = Seat.create(
                command.roomId(),
                command.seatId()
        );

        seatStore.save(seat);

        return true;
    }

    @Override
    public boolean update(SeatUpdateCommand command){
        Seat seat = seatStore.findByRoomIdAndSeatId(command.roomId(), command.seatId()).orElseThrow();

        if(seatStore.existsSeatConflict(command.roomId(), command.seatId())){
            return false;
        }

        seat.update(command.roomId(), command.seatId());

        return true;
    }

    @Override
    public boolean delete(String roomId, String seatId){

        seatStore.deleteByRoomIdAndSeatId(roomId, seatId);

        return true;
    }
}
