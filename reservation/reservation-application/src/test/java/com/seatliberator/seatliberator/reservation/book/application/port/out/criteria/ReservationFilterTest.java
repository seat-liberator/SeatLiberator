package com.seatliberator.seatliberator.reservation.book.application.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Reservation Filter")
public class ReservationFilterTest {
    @Test
    @DisplayName("empty는 모든 필터 조건이 비어있다")
    void empty() {
        var filter = ReservationFilter.empty();

        assertThat(filter.userIds()).isEmpty();
        assertThat(filter.excludedIds()).isEmpty();
        assertThat(filter.statuses()).isEmpty();
    }

    @Test
    @DisplayName("withUserId는 userIds만 교체하고 나머지는 유지한다")
    void with_user_ids() {
        var filter = new ReservationFilter(
                Set.of("user-1"),
                Set.of(1L, 2L),
                Set.of(ReservationStatus.RESERVED)
        );

        var updated = filter.withUserIds("user-2", "user-3");

        assertThat(updated.userIds()).containsExactlyInAnyOrder("user-2", "user-3");
        assertThat(updated.excludedIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(updated.statuses()).containsExactly(ReservationStatus.RESERVED);
    }

    @Test
    @DisplayName("withExcludedIds는 excludedIds만 교체하고 나머지는 유지한다")
    void with_excluded_ids() {
        var filter = new ReservationFilter(
                Set.of("user-1"),
                Set.of(1L, 2L),
                Set.of(ReservationStatus.RESERVED)
        );

        var updated = filter.withExcludeIds(10L, 20L);

        assertThat(updated.userIds()).containsExactlyInAnyOrder("user-1");
        assertThat(updated.excludedIds()).containsExactlyInAnyOrder(10L, 20L);
        assertThat(updated.statuses()).containsExactlyInAnyOrder(ReservationStatus.RESERVED);
    }

    @Test
    @DisplayName("withStatuses는 statuses만 교체하고 나머지는 유지한다")
    void with_statuses() {
        var filter = new ReservationFilter(
                Set.of("user-1"),
                Set.of(1L),
                Set.of(ReservationStatus.RESERVED)
        );

        var updated = filter.withStatuses(ReservationStatus.CANCELED);

        assertThat(updated.userIds()).containsExactly("user-1");
        assertThat(updated.excludedIds()).containsExactly(1L);
        assertThat(updated.statuses()).containsExactly(ReservationStatus.CANCELED);
    }

    @Test
    @DisplayName("with 메서드들은 새로운 객체를 반환한다")
    void with_methods_return_new_instance() {
        var filter = ReservationFilter.empty();

        var updated = filter.withUserIds("user-1");

        assertThat(updated).isNotSameAs(filter);
    }
}
