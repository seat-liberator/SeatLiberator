package com.seatliberator.seatliberator.reservation.application.waitlist.port.in;

import com.seatliberator.seatliberator.reservation.application.waitlist.port.in.command.CreateWaitlistCommand;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;

public interface CreateWaitlistUseCase {
    Waitlist create(CreateWaitlistCommand command);
}
