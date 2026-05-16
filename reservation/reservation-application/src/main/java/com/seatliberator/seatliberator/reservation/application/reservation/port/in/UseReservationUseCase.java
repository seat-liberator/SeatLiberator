package com.seatliberator.seatliberator.reservation.application.reservation.port.in;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;

public interface UseReservationUseCase {
    ReservationResult use(UseReservationCommand command);
}
