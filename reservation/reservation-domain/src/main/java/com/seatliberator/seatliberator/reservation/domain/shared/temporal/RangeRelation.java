package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

public enum RangeRelation {
    SAME,
    CONTAINS,
    CONTAINED_BY,
    BEFORE,
    AFTER,
    IMMEDIATELY_BEFORE,
    IMMEDIATELY_AFTER,
    OVERLAPS_AFTER,
    OVERLAPS_BEFORE
}
