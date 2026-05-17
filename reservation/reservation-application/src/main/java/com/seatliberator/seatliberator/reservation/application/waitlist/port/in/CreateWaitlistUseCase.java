package com.seatliberator.seatliberator.reservation.application.waitlist.port.in;

import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CreateWaitlistCommand;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.result.WaitlistResult;

public interface CreateWaitlistUseCase {
    WaitlistResult create(CreateWaitlistCommand command);
}
