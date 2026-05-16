package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import java.time.LocalDate;

public class DateRangeTestSupport {
    public static final LocalDate START_AT = LocalDate.of(2026, 1, 10);
    public static final LocalDate END_AT = LocalDate.of(2026, 1, 20);

    public static final LocalDate BEFORE_START_AT = START_AT.minusDays(3);
    public static final LocalDate AFTER_START_AT = START_AT.plusDays(3);
    public static final LocalDate BEFORE_END_AT = END_AT.minusDays(3);
    public static final LocalDate AFTER_END_AT = END_AT.plusDays(3);
}
