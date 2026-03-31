package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationUpdateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationEntry;

public interface ReservationManager {

    ReservationEntry create(ReservationCreateCommand command);

    ReservationEntry update(ReservationUpdateCommand command);

    ReservationEntry cancel(String userId);
}
