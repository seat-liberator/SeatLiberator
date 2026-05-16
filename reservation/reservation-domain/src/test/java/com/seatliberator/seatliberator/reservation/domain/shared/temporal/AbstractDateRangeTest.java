package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.DateRangeTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractDateRangeTest<T extends DateRange> extends AbstractRangeComparableTest<DateRange> {

    public abstract T create(LocalDate startAt, LocalDate endAt);

    Stream<Arguments> nullArgumentCases() {
        return Stream.of(
                arguments("startAt = null", (Supplier<T>) () -> create(null, END_AT), "startAt"),
                arguments("endAt = null", (Supplier<T>) () -> create(START_AT, null), "endAt")
        );
    }

    @BeforeEach
    void run() {
        range = create(START_AT, END_AT);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("nullArgumentCases")
    @DisplayName("인자가 null이면 예외")
    void throw_exception_when_required_argument_is_null(
            String displayName,
            Supplier<T> supplier,
            String fieldName
    ) {
        assertThatDomainThrownBy(supplier::get)
                .hasNonNullMessageFor(fieldName);
    }

    @Test
    @DisplayName("시작 날짜와 종료 날짜로 날짜 구간을 생성할 수 있다")
    void create_with_start_at_and_end_at() {
        assertThat(range.startAt()).isEqualTo(START_AT);
        assertThat(range.endAt()).isEqualTo(END_AT);
    }

    @Test
    @DisplayName("시작 날짜가 종료 날짜보다 같거나 이후면 예외")
    void throw_exception_when_startAt_is_after_endAt() {
        assertThatThrownBy(() -> create(START_AT, START_AT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startAt must be before endAt.");

        assertThatThrownBy(() -> create(START_AT, BEFORE_START_AT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startAt must be before endAt.");
    }

    @Override
    protected T createSameRange() {
        return create(START_AT, END_AT);
    }

    @Override
    protected T createContainedRange() {
        return create(AFTER_START_AT, BEFORE_END_AT);
    }

    @Override
    protected T createContainingRange() {
        return create(BEFORE_START_AT, AFTER_END_AT);
    }

    @Override
    protected T createBeforeRange() {
        return create(BEFORE_START_AT.minusDays(3), BEFORE_START_AT);
    }

    @Override
    protected T createImmediatelyBeforeRange() {
        return create(END_AT, AFTER_END_AT);
    }

    @Override
    protected T createAfterRange() {
        return create(AFTER_END_AT, AFTER_END_AT.plusDays(3));
    }

    @Override
    protected T createImmediatelyAfterRange() {
        return create(BEFORE_START_AT, START_AT);
    }

    @Override
    protected T createOverlapsBeforeRange() {
        return create(BEFORE_START_AT, AFTER_START_AT);
    }

    @Override
    protected T createOverlapsAfterRange() {
        return create(BEFORE_END_AT, AFTER_END_AT);
    }
}
