package com.seatliberator.seatliberator.reservation.application.reservation.port.out.criteria;

import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record ReservationFilter(
        Set<String> userIds,
        Set<UUID> excludedIds,
        Set<ReservationStatus> statuses
) {
    public ReservationFilter {
        userIds = Set.copyOf(Objects.requireNonNull(userIds));
        excludedIds = Set.copyOf(Objects.requireNonNull(excludedIds));
        statuses = Set.copyOf(Objects.requireNonNull(statuses));
    }

    public static ReservationFilter empty() {
        return new ReservationFilter(Set.of(), Set.of(), Set.of());
    }

    public ReservationFilter withUserIds(Collection<String> userIds) {
        return new ReservationFilter(Set.copyOf(userIds), excludedIds, statuses);
    }

    public ReservationFilter withUserIds(String... userIds) {
        return new ReservationFilter(Set.of(userIds), excludedIds, statuses);
    }

    public ReservationFilter withExcludeIds(Collection<UUID> ids) {
        return new ReservationFilter(userIds, Set.copyOf(ids), statuses);
    }

    public ReservationFilter withExcludeIds(UUID... ids) {
        return new ReservationFilter(userIds, Set.of(ids), statuses);
    }

    public ReservationFilter withStatuses(Collection<ReservationStatus> statuses) {
        return new ReservationFilter(userIds, excludedIds, Set.copyOf(statuses));
    }

    public ReservationFilter withStatuses(ReservationStatus... statuses) {
        return new ReservationFilter(userIds, excludedIds, Set.of(statuses));
    }
}
