package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRangeTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractInstantRangeTest<T extends InstantRange> extends AbstractRangeComparableTest<InstantRange> {

    public abstract T create(Instant startAt, Instant endAt);

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
    @DisplayName("시작 시간과 종료 시간으로 시간 구간을 생성할 수 있다")
    void create_with_start_at_and_end_at() {
        assertThat(range.startAt()).isEqualTo(START_AT);
        assertThat(range.endAt()).isEqualTo(END_AT);
        assertThat(range.duration()).isEqualTo(DURATION);
    }

    @Test
    @DisplayName("시작 시간이 종료 시간보다 같거나 이후면 예외")
    void throw_exception_when_startAt_is_after_endAt() {
        assertThatThrownBy(() -> create(START_AT, START_AT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startAt must be before endAt.");

        assertThatThrownBy(() -> create(START_AT, BEFORE_START_AT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startAt must be before endAt.");
    }

    @Test
    @DisplayName("Instant 포함 여부는 시작 경계는 포함하고, 종료 경계는 포함하지 않는다")
    void contains_instant_start_boundary_but_not_end_boundary() {
        assertThat(range.contains(START_AT)).isTrue();
        assertThat(range.contains(END_AT)).isFalse();
        assertThat(range.contains(BEFORE_START_AT)).isFalse();
        assertThat(range.contains(AFTER_END_AT)).isFalse();
    }

    @Test
    @DisplayName("Instant가 null이면 포함 여부 확인 시 예외")
    void throw_exception_when_instant_is_null() {
        assertThatDomainThrownBy(() -> range.contains((Instant) null))
                .hasNonNullMessageFor("other");
    }

    @Test
    @DisplayName("종료 시각 도달 여부는 종료 경계를 포함해 확인한다")
    void is_ended_contains_end_boundary() {
        assertThat(range.isEnded(BEFORE_END_AT)).isFalse();

        assertThat(range.isEnded(END_AT)).isTrue();
        assertThat(range.isEnded(AFTER_END_AT)).isTrue();
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
        return create(BEFORE_START_AT, START_AT);
    }

    @Override
    protected T createAfterRange() {
        return create(END_AT, AFTER_END_AT);
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
