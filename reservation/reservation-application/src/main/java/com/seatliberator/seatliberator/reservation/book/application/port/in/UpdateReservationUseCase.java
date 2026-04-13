package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.UpdateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.result.ReservationResult;

public interface UpdateReservationUseCase {
    ReservationResult update(UpdateReservationCommand command);
}
