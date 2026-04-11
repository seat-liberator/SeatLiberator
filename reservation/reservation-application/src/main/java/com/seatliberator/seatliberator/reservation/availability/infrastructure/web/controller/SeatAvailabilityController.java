package com.seatliberator.seatliberator.reservation.availability.infrastructure.web.controller;

import com.seatliberator.seatliberator.reservation.availability.application.port.in.SeatAvailabilityReader;
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
    private final SeatAvailabilityReader reader;

    @GetMapping("/{roomId}/available-seats")
    public ResponseEntity<?> getAvailableSeats(
            @PathVariable("roomId") String roomId,
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        var range = SimpleTimeRange.from(start, end);
        var result = reader.findAvailabilitySeats(roomId, range);
        return ResponseEntity.ok(result);
    }
}
