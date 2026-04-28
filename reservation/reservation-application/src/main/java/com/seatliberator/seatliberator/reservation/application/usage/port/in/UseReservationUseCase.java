package com.seatliberator.seatliberator.reservation.application.usage.port.in;

import com.seatliberator.seatliberator.reservation.application.usage.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.usage.port.in.result.UseReservationResult;

public interface UseReservationUseCase {
    UseReservationResult use(UseReservationCommand command);
}
