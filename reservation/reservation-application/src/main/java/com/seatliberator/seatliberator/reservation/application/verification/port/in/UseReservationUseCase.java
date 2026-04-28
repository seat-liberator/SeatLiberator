package com.seatliberator.seatliberator.reservation.application.verification.port.in;

import com.seatliberator.seatliberator.reservation.application.verification.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.verification.port.in.result.UseReservationResult;

public interface UseReservationUseCase {
    UseReservationResult use(UseReservationCommand command);
}
