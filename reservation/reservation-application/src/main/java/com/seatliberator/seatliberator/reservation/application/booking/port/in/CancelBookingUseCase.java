package com.seatliberator.seatliberator.reservation.application.booking.port.in;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CancelBookingCommand;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;

public interface CancelBookingUseCase {
    ReservationResult cancel(CancelBookingCommand command);
}
