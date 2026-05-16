package com.seatliberator.seatliberator.reservation.application.occupancy.port.out;

import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;

import java.util.Collection;
import java.util.List;

public interface SeatOccupancyStore {
    SeatOccupancy save(SeatOccupancy seatOccupancy);

    List<SeatOccupancy> saveAll(Collection<SeatOccupancy> seatOccupancies);

    void delete(SeatOccupancy seatOccupancy);
}
