package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public interface ActiveInactiveTransitionContractTest<T> {
    Instant now = TestSupport.fixedClock.instant();

    T createActive(Instant createdAt);

    T createInactive(Instant createdAt);

    T activate(T domain, Instant activatedAt);

    T inactivate(T domain, Instant inactivatedAt);

    boolean isActive(T domain);

    Instant getCreatedAt(T domain);

    Instant getLastActivatedAt(T domain);

    Instant getLastInactivatedAt(T domain);

    @Test
    @DisplayName("활성화 상태에서 비활성화 가능")
    default void transition_active_to_inactive() {
        var d = createActive(now);
        inactivate(d, now.plusSeconds(5));
        assertThat(isActive(d)).isFalse();
    }

    @Test
    @DisplayName("활성화 상태에서 다시 활성화시 예외")
    default void throw_exception_when_transition_active_to_active() {
        var d = createActive(now);
        var s = snapshot(d);

        assertThatThrownBy(() -> activate(d, now.plusSeconds(5)))
                .isInstanceOf(IllegalStateException.class);
        assertUnchanged(d, s);
    }

    @Test
    @DisplayName("활성화 시 직전 활성화 시각은 새로운 활성화 시각으로 갱신된다.")
    default void update_lastActivatedAt_to_activatedAt_when_activate() {
        var d = createInactive(now);
        var beforeLastInactivatedAt = getLastInactivatedAt(d);
        var activatedAt = now.plusSeconds(5);

        activate(d, activatedAt);

        assertThat(isActive(d)).isTrue();
        assertThat(getLastActivatedAt(d)).isEqualTo(activatedAt);
        assertThat(getLastInactivatedAt(d)).isEqualTo(beforeLastInactivatedAt);
    }

    @Test
    @DisplayName("활성화 시, 활성화 시각이 직전 비활성화 시각보다 과거면 예외")
    default void throw_exception_when_activatedAt_is_before_than_lastInactivatedAt() {
        var d = createActive(now);
        var inactivatedAt = now.plusSeconds(5);
        inactivate(d, inactivatedAt);
        var s = snapshot(d);

        assertThatThrownBy(() -> activate(d, inactivatedAt.minusSeconds(3)))
                .isInstanceOf(IllegalArgumentException.class);
        assertUnchanged(d, s);
    }

    @Test
    @DisplayName("활성화 시 활성 시각이 생성 시각보다 과거면 예외")
    default void throw_exception_when_activatedAt_is_before_than_createdAt() {
        var d = createInactive(now);
        var before = snapshot(d);

        assertThatThrownBy(() -> activate(d, now.minusSeconds(5)))
                .isInstanceOf(IllegalArgumentException.class);
        assertUnchanged(d, before);
    }

    @Test
    @DisplayName("생성 시각과 같은 시각에 활성화 가능")
    default void activate_with_activatedAt_as_same_as_createdAt() {
        var d = createInactive(now);

        activate(d, now);

        assertThat(isActive(d)).isTrue();
        assertThat(getLastActivatedAt(d)).isEqualTo(now);
    }

    @Test
    @DisplayName("비활성화 상태에서 활성화 가능")
    default void transition_inactive_to_active() {
        var d = createInactive(now);
        activate(d, now.plusSeconds(5));
        assertThat(isActive(d)).isTrue();
    }

    @Test
    @DisplayName("비활성화 상태에서 다시 비활성화시 예외")
    default void throw_exception_when_transition_inactive_to_inactive() {
        var d = createInactive(now);
        var s = snapshot(d);

        assertThatThrownBy(() -> inactivate(d, now.plusSeconds(5)))
                .isInstanceOf(IllegalStateException.class);
        assertUnchanged(d, s);
    }

    @Test
    @DisplayName("비활성화 시 직전 비활성화 시각은 새로운 비활성화 시각으로 갱신된다.")
    default void update_lastInactivatedAt_to_inactivatedAt_when_inactivate() {
        var d = createActive(now);
        var beforeLastActivatedAt = getLastActivatedAt(d);
        var inactivatedAt = now.plusSeconds(5);

        inactivate(d, inactivatedAt);

        assertThat(isActive(d)).isFalse();
        assertThat(getLastInactivatedAt(d)).isEqualTo(inactivatedAt);
        assertThat(getLastActivatedAt(d)).isEqualTo(beforeLastActivatedAt);
    }

    @Test
    @DisplayName("비활성화 시, 비활성화 시각이 직전 활활성화 시각보다 과거면 예외")
    default void throw_exception_when_inactivatedAt_is_before_than_lastActivatedAt() {
        var d = createInactive(now);
        var activatedAt = now.plusSeconds(5);
        activate(d, activatedAt);
        var s = snapshot(d);

        assertThatThrownBy(() -> inactivate(d, activatedAt.minusSeconds(3)))
                .isInstanceOf(IllegalArgumentException.class);
        assertUnchanged(d, s);
    }

    @Test
    @DisplayName("비활성화 시 비활성 시각이 생성 시각보다 과거면 예외")
    default void throw_exception_when_inactivatedAt_is_before_than_createdAt() {
        var d = createActive(now);
        var before = snapshot(d);

        assertThatThrownBy(() -> inactivate(d, now.minusSeconds(5)))
                .isInstanceOf(IllegalArgumentException.class);
        assertUnchanged(d, before);
    }

    @Test
    @DisplayName("생성 시각과 같은 시각에 비활성화 가능")
    default void inactivate_with_inactivatedAt_as_same_as_createdAt() {
        var d = createActive(now);

        inactivate(d, now);

        assertThat(isActive(d)).isFalse();
        assertThat(getLastInactivatedAt(d)).isEqualTo(now);
    }

    private ActiveInactiveSnapshot snapshot(T domain) {
        return new ActiveInactiveSnapshot(
                isActive(domain),
                getLastActivatedAt(domain),
                getLastInactivatedAt(domain)
        );
    }

    private void assertUnchanged(T domain, ActiveInactiveSnapshot before) {
        assertThat(snapshot(domain)).isEqualTo(before);
    }

    record ActiveInactiveSnapshot(
            boolean active,
            Instant lastActivatedAt,
            Instant lastInactivatedAt
    ) {
    }
}
