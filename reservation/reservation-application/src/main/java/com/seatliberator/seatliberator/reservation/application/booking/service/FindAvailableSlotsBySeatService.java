package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindAvailableSlotsBySeatUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindAvailableSlotsBySeatQuery;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyReader;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancyFilter;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancySlotCriteria;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotFilter;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindAvailableSlotsBySeatService implements FindAvailableSlotsBySeatUseCase {
    private final SeatReader seatReader;
    private final SeatTimeSlotReader slotReader;
    private final SeatOccupancyReader occupancyReader;

    @Override
    public Map<LocalDate, List<SeatTimeSlotResult>> findAtDateRange(FindAvailableSlotsBySeatQuery query) {
        var seatId = query.seatId();
        var existsSeat = seatReader.existsById(seatId);
        if (!existsSeat) throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        var range = query.range();
        var filter = SeatTimeSlotFilter.empty().seatId(seatId);
        var slots = slotReader.findByFilter(filter);
        var slotIds = slots.stream().map(SeatTimeSlot::getId).toList();

        var criteria = SeatOccupancySlotCriteria
                .matchAnyOf(slotIds)
                .filter(SeatOccupancyFilter.empty().range(range));
        var occupancies = occupancyReader.findByCriteria(criteria).stream()
                .collect(Collectors.groupingBy(
                        SeatOccupancy::getOccupancyDate,
                        Collectors.mapping(SeatOccupancy::getSeatTimeSlotId, Collectors.toSet())
                ));

        return range.stream()
                .collect(Collectors.toMap(
                        date -> date,
                        date -> slots.stream()
                                .filter(slot -> !occupancies.getOrDefault(date, Set.of()).contains(slot.getId()))
                                .map(SeatTimeSlotResult::from)
                                .toList(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }
}
