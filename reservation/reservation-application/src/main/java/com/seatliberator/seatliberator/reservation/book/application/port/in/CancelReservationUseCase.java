package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CancelReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.result.ReservationResult;

public interface CancelReservationUseCase {
    ReservationResult cancel(CancelReservationCommand command);
}
