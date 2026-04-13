package com.seatliberator.seatliberator.reservation.book.application.port.in;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.DeleteSeatCommand;

public interface DeleteSeatUseCase {
    boolean delete(DeleteSeatCommand command);
}
