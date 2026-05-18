package com.seatliberator.seatliberator.reservation.application.occupancy;

import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.DefaultTestSupport;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class OccupancyTestSupport extends DefaultTestSupport {
    public static final UUID RESERVATION_ID = UuidGenerator.generate(1);
    public static final UUID MORNING_SLOT_ID = UuidGenerator.generate(2);
    public static final UUID AFTERNOON_SLOT_ID = UuidGenerator.generate(3);

    public static final List<UUID> SLOT_IDS = List.of(MORNING_SLOT_ID, AFTERNOON_SLOT_ID);
    public static final LocalDate OCCUPANCY_DATE = LocalDate.now(CLOCK);

    public static SeatOccupancy occupancy(UUID slotId) {
        return occupancy(slotId, OCCUPANCY_DATE);
    }

    public static SeatOccupancy occupancy(UUID slotId, LocalDate occupancyDate) {
        return SeatOccupancy.of(slotId, RESERVATION_ID, occupancyDate, NOW);
    }

    public static List<SeatOccupancy> occupancies() {
        return SLOT_IDS.stream()
                .map(OccupancyTestSupport::occupancy)
                .toList();
    }
}
