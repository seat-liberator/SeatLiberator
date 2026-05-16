package com.seatliberator.seatliberator.reservation.application.seat.port.out;

import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatTimeSlotReader {
    boolean existsById(UUID id);

    Optional<SeatTimeSlot> findById(UUID id);

    List<SeatTimeSlot> findByIds(Collection<UUID> ids);

    List<SeatTimeSlot> findBySeatId(UUID seatId);
}
