package com.seatliberator.seatliberator.reservation.application.waitlist.port.in;

import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CancelWaitlistCommand;

public interface CancelWaitlistUseCase {
    void cancel(CancelWaitlistCommand command);
}
