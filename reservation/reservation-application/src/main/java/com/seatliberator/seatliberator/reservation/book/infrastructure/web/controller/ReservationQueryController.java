package com.seatliberator.seatliberator.reservation.book.infrastructure.web.controller;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.book.application.port.in.FindMyReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.query.FindMyReservationQuery;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationQueryController {

    private final FindMyReservationUseCase findMyReservationUseCase;

    private final ActorContextHolder actorContextHolder;

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startAt,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endAt,
            @RequestParam(name = "status") ReservationStatus status
    ) {
        var userId = actorContextHolder.getActor().subject();
        var range = SimpleTimeRange.from(startAt, endAt);
        var query = new FindMyReservationQuery(userId, range, status);
        var result = findMyReservationUseCase.find(query);
        return ResponseEntity.ok(result);
    }
}
