package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindAvailableSlotsBySeatUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindAvailableSlotsBySeatQuery;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyReader;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancyFilter;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
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
        var range = query.range();

        var slots = slotReader.findBySeatId(seatId);
        var slotIds = slots.stream().map(SeatTimeSlot::getId).toList();

        var occupancyFilter = SeatOccupancyFilter.builder()
                .slotIds(slotIds)
                .range(range)
                .build();
        var occupancies = occupancyReader.findByCriteria(occupancyFilter).stream()
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
