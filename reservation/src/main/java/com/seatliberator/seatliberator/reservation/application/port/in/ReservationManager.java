package com.seatliberator.seatliberator.reservation.application.port.in;

import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationUpdateCommand;

public interface ReservationManager {

    boolean create(ReservationCreateCommand command);

    boolean update(ReservationUpdateCommand command);

    boolean cancel(String userId);
}
