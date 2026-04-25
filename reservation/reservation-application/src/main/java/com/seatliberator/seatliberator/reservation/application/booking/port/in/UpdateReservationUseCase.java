package com.seatliberator.seatliberator.reservation.application.booking.port.in;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.UpdateReservationCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;

public interface UpdateReservationUseCase {
    ReservationResult update(UpdateReservationCommand command);
}
