package com.seatliberator.seatliberator.reservation.application.port.in;

import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatUpdateCommand;

public interface SeatManager {

    boolean create(SeatCreateCommand command);

    boolean update(SeatUpdateCommand command);

    boolean delete(String roomId, String seatId);
}
