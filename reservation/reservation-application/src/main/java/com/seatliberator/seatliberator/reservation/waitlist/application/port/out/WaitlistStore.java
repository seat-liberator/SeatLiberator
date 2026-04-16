package com.seatliberator.seatliberator.reservation.waitlist.application.port.out;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.WaitlistStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaitlistStore {

    boolean existsByUserIdAndLocatorAndRangeAndStatus(String userId, SeatLocator locator, TimeRange range, WaitlistStatus status);

    Optional<Waitlist> findById(UUID id);

    List<Waitlist> findByLocatorAndRange(SeatLocator locator, TimeRange range);

    List<Waitlist> findByLocatorAndRangeAndStatus(SeatLocator locator, TimeRange range, WaitlistStatus status);

    Waitlist save(Waitlist waitlist);

    List<Waitlist> saveAll(Iterable<Waitlist> vacancyAlertRequests);
}
