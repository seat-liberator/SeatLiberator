package com.seatliberator.seatliberator.reservation.launcher.seed;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "seatliberator.reservation.launcher.seed")
public record ReservationApplicationSeedProperties(
        @Valid
        @NotNull
        @DefaultValue
        Room room,

        @Valid
        @NotNull
        @DefaultValue
        Seat seat,

        @Valid
        @NotNull
        @DefaultValue
        SeatTimeSlot seatTimeSlot
) {
    public record Room(
            @NotBlank
            @DefaultValue("Room-%s")
            String codePrefixFormat,

            @PositiveOrZero
            @DefaultValue("5")
            int num
    ) {
    }

    public record Seat(
            @NotBlank
            @DefaultValue("Seat-%s")
            String codePrefixFormat,

            @PositiveOrZero
            @DefaultValue("10")
            int num
    ) {
    }

    public record SeatTimeSlot(
            @NotNull
            @DefaultValue("PT50M")
            Duration duration,

            @NotEmpty
            @DefaultValue({"08:00:00", "09:00:00", "10:00:00", "11:00:00", "13:00:00", "14:00:00", "15:00:00"})
            List<@NotNull LocalTime> schedule
    ) {
    }
}
