package com.seatliberator.seatliberator.reservation.application.booking.port.in;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindBookingQuery;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;

public interface FindBookingUseCase {
    BookingDetailResult find(FindBookingQuery query);
}
