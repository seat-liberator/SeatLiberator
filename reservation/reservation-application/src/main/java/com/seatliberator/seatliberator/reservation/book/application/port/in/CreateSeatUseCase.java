package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateSeatCommand;

public interface CreateSeatUseCase {
    boolean create(CreateSeatCommand command);
}
