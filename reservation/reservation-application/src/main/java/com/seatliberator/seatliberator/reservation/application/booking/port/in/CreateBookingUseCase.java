package com.seatliberator.seatliberator.reservation.application.booking.port.in;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateBookingCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingResult;

public interface CreateBookingUseCase {
    BookingResult create(CreateBookingCommand command);
}
