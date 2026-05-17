package com.seatliberator.seatliberator.reservation.application.waitlist.port.out;

import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;

import java.util.List;

public interface WaitlistStore {
    Waitlist save(Waitlist waitlist);

    List<Waitlist> saveAll(Iterable<Waitlist> waitlists);
}
