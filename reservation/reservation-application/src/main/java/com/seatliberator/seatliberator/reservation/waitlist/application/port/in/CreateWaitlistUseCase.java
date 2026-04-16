package com.seatliberator.seatliberator.reservation.waitlist.application.port.in;

import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CreateWaitlistCommand;

public interface CreateWaitlistUseCase {
    Waitlist create(CreateWaitlistCommand command);
}
