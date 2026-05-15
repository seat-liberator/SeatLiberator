package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.List;

public record SimpleDailySchedule(
        List<DailyNanoRange> ranges
) implements DailySchedule {
    public SimpleDailySchedule {
        ranges = DailySchedule.validateAndSort(ranges);
    }

    public static <T extends DailyNanoRange> SimpleDailySchedule of(List<T> ranges) {
        return new SimpleDailySchedule(ranges.stream().<DailyNanoRange>map(SimpleDailyNanoRange::from).toList());
    }

    public static SimpleDailySchedule from(DailySchedule schedule) {
        Preconditions.requireNonNull(schedule, "schedule");

        return of(schedule.ranges());
    }
}
