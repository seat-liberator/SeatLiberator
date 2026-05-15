package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RangeComparable 계약 테스트")
public abstract class AbstractRangeComparableTest<T extends RangeComparable<? super T>> {

    protected T range;

    protected abstract T createSameRange();

    protected abstract T createContainedRange();

    protected abstract T createContainingRange();

    protected abstract T createBeforeRange();

    protected abstract T createAfterRange();

    protected abstract T createOverlapsBeforeRange();

    protected abstract T createOverlapsAfterRange();

    @Test
    @DisplayName("같은 구간인지 확인할 수 있다")
    void same_range() {
        assertThat(range.isSame(createSameRange())).isTrue();
        assertThat(range.isSame(createContainingRange())).isFalse();

        assertThat(range.relationTo(createSameRange())).isEqualTo(RangeRelation.SAME);
    }

    @Test
    @DisplayName("시작 지점이 더 앞서는지 확인할 수 있다")
    void starts_before_range() {
        assertThat(range.startsBefore(createAfterRange())).isTrue();
        assertThat(range.startsBefore(createOverlapsAfterRange())).isTrue();

        assertThat(range.startsBefore(createBeforeRange())).isFalse();
        assertThat(range.startsBefore(createOverlapsBeforeRange())).isFalse();
    }

    @Test
    @DisplayName("종료 지점이 더 뒤서는지 확인할 수 있다")
    void ends_after_range() {
        assertThat(range.endsAfter(createBeforeRange())).isTrue();
        assertThat(range.endsAfter(createOverlapsBeforeRange())).isTrue();

        assertThat(range.endsAfter(createAfterRange())).isFalse();
        assertThat(range.endsAfter(createOverlapsAfterRange())).isFalse();
    }

    @Test
    @DisplayName("다른 구간을 포함하는지 확인할 수 있다")
    void contains_range() {
        assertThat(range.contains(createSameRange())).isTrue();
        assertThat(range.contains(createContainedRange())).isTrue();

        assertThat(range.contains(createContainingRange())).isFalse();
        assertThat(range.contains(createBeforeRange())).isFalse();
        assertThat(range.contains(createAfterRange())).isFalse();

        assertThat(range.relationTo(createContainedRange())).isEqualTo(RangeRelation.CONTAINS);
    }

    @Test
    @DisplayName("다른 구간에 포함되는지 확인할 수 있다")
    void contained_by_range() {
        assertThat(range.containsBy(createSameRange())).isTrue();
        assertThat(range.containsBy(createContainingRange())).isTrue();

        assertThat(range.containsBy(createContainedRange())).isFalse();
        assertThat(range.containsBy(createBeforeRange())).isFalse();
        assertThat(range.containsBy(createAfterRange())).isFalse();

        assertThat(range.relationTo(createContainingRange())).isEqualTo(RangeRelation.CONTAINED_BY);
    }

    @Test
    @DisplayName("다른 구간과 겹치는지 확인할 수 있다")
    void overlaps_range() {
        assertThat(range.overlaps(createSameRange())).isTrue();
        assertThat(range.overlaps(createContainedRange())).isTrue();
        assertThat(range.overlaps(createContainingRange())).isTrue();
        assertThat(range.overlaps(createOverlapsBeforeRange())).isTrue();
        assertThat(range.overlaps(createOverlapsAfterRange())).isTrue();

        assertThat(range.overlaps(createBeforeRange())).isFalse();
        assertThat(range.overlaps(createAfterRange())).isFalse();
    }

    @Test
    @DisplayName("다른 구간과의 관계를 확인할 수 있다")
    void relation_to_range() {
        assertThat(range.relationTo(createBeforeRange())).isEqualTo(RangeRelation.AFTER);
        assertThat(range.relationTo(createAfterRange())).isEqualTo(RangeRelation.BEFORE);
        assertThat(range.relationTo(createOverlapsBeforeRange())).isEqualTo(RangeRelation.OVERLAPS_AFTER);
        assertThat(range.relationTo(createOverlapsAfterRange())).isEqualTo(RangeRelation.OVERLAPS_BEFORE);
    }

    @Test
    @DisplayName("비교 대상이 null이면 예외")
    void throw_exception_when_other_is_null() {
        assertThatDomainThrownBy(() -> range.relationTo(null))
                .hasNonNullMessageFor("other");
    }
}
