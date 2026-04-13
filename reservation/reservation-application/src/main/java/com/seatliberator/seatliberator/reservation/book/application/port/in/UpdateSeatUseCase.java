package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.UpdateSeatCommand;

public interface UpdateSeatUseCase {
    boolean update(UpdateSeatCommand command);
}
