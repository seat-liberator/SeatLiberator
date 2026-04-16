package com.seatliberator.seatliberator.reservation.shared.application.seed;

import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.SeatStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatSeedService {
    private final SeatStore store;
    private final SeatReader query;

    public void seed() {
        var seats = DevFixture.createSeats();
        for (var seat : seats) {
            if (createIfNotExists(seat)) {
                var locator = seat.getLocator();
                log.debug("Seat seed data created. roomId={}, seatId={}", locator.roomId(), locator.seatId());
            }
        }
    }

    private boolean createIfNotExists(Seat seat) {
        if (query.findByLocator(seat.getLocator()).isPresent()) {
            return false;
        }
        store.save(seat);
        return true;
    }
}
