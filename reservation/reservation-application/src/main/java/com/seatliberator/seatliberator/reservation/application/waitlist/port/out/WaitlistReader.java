package com.seatliberator.seatliberator.reservation.application.waitlist.port.out;

import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter.WaitlistFilter;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter.WaitlistOrder;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaitlistReader {
    boolean existsById(UUID id);

    boolean existsByFilter(WaitlistFilter filter);

    Optional<Waitlist> findById(UUID id);

    List<Waitlist> findByFilter(WaitlistFilter filter, WaitlistOrder order);
}
