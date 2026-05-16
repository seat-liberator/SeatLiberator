package com.seatliberator.seatliberator.reservation.application.booking.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateBookingCommand(
        String userId,
        List<UUID> seatTimeSlotIds,
        LocalDate occupancyDate
) {
    public CreateBookingCommand {
        Preconditions.requireNonBlank(userId, "userId");
        Preconditions.requireNonNull(seatTimeSlotIds, "seatTimeSlotIds");
        Preconditions.requireNonNull(occupancyDate, "occupancyDate");
    }

    public static CreateBookingCommand of(String userId, List<UUID> seatTimeSlotIds, LocalDate occupancyDate) {
        return new CreateBookingCommand(userId, seatTimeSlotIds, occupancyDate);
    }
}
