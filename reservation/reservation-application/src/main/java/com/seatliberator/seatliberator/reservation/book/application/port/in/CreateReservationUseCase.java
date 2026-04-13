package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.result.ReservationResult;

public interface CreateReservationUseCase {
    ReservationResult create(CreateReservationCommand command);
}
