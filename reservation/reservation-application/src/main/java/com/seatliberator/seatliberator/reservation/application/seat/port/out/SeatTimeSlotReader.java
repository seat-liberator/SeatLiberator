package com.seatliberator.seatliberator.reservation.application.seat.port.out;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotFilter;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotRangeOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatTimeSlotReader {
    boolean existsById(UUID id);

    boolean existsByCriteria(SeatTimeSlotRangeOverlapCriteria criteria);

    Optional<SeatTimeSlot> findById(UUID id);

    List<SeatTimeSlot> findByIds(Collection<UUID> ids);

    List<SeatTimeSlot> findByFilter(SeatTimeSlotFilter filter);
}
