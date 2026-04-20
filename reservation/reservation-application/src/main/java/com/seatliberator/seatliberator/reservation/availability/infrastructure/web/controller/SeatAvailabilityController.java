package com.seatliberator.seatliberator.reservation.availability.infrastructure.web.controller;

import com.seatliberator.seatliberator.reservation.availability.application.port.in.FindAvailableSeatsUseCase;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.FindSeatOccupancyRangesUseCase;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.FindSeatStatusesUseCase;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindAvailableSeatQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindOccupancyRangesQuery;
import com.seatliberator.seatliberator.reservation.availability.application.port.in.query.FindSeatStatusesQuery;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class SeatAvailabilityController {
    private final FindAvailableSeatsUseCase findAvailableSeatsUseCase;
    private final FindSeatStatusesUseCase findSeatStatusesUseCase;
    private final FindSeatOccupancyRangesUseCase findSeatOccupancyRangesUseCase;

    @GetMapping("/{roomId}/available-seats")
    public ResponseEntity<?> getAvailableSeats(
            @PathVariable("roomId") String roomId,
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startAt,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endAt
    ) {
        var range = SimpleTimeRange.of(startAt, endAt);
        var query = new FindAvailableSeatQuery(roomId, range);
        var result = findAvailableSeatsUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{roomId}/seat-statuses")
    public ResponseEntity<?> getSeatStatuses(
            @PathVariable("roomId") String roomId,
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startAt,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endAt
    ) {
        var range = SimpleTimeRange.of(startAt, endAt);
        var query = new FindSeatStatusesQuery(roomId, range);
        var result = findSeatStatusesUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{roomId}/seats/{seatId}/occupy")
    public ResponseEntity<?> getSeatOccupiedRange(
            @PathVariable("roomId") String roomId,
            @PathVariable("seatId") String seatId,
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startAt,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endAt
    ) {
        var locator = SimpleSeatLocator.of(roomId, seatId);
        var range = SimpleTimeRange.of(startAt, endAt);
        var query = new FindOccupancyRangesQuery(locator, range);
        var result = findSeatOccupancyRangesUseCase.find(query);
        return ResponseEntity.ok(result);
    }
}
