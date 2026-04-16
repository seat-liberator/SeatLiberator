package com.seatliberator.seatliberator.reservation.waitlist.application.port.in;

import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CancelWaitlistCommand;

public interface CancelWaitlistUseCase {
    void cancel(CancelWaitlistCommand command);
}
