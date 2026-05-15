package com.seatliberator.seatliberator.reservation.application.waitlist.port.out;

import com.seatliberator.seatliberator.reservation.domain.shared.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaitlistStore {

    boolean existsByUserIdAndLocatorAndRangeAndStatus(String userId, SeatLocator locator, InstantRange range, WaitlistStatus status);

    Optional<Waitlist> findById(UUID id);

    List<Waitlist> findByLocatorAndRange(SeatLocator locator, InstantRange range);

    List<Waitlist> findByLocatorAndRangeAndStatus(SeatLocator locator, InstantRange range, WaitlistStatus status);

    Waitlist save(Waitlist waitlist);

    List<Waitlist> saveAll(Iterable<Waitlist> waitlists);
}
