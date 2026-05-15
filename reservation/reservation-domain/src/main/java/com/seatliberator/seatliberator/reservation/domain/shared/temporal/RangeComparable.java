package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

public interface RangeComparable<T> {
    boolean isSame(T other);

    boolean startsBefore(T other);

    boolean immediatelyBefore(T other);

    boolean endsAfter(T other);

    boolean immediatelyAfter(T other);

    boolean contains(T other);

    boolean containsBy(T other);

    boolean overlaps(T other);

    default RangeRelation relationTo(T other) {
        if (isSame(other)) return RangeRelation.SAME;

        if (contains(other)) return RangeRelation.CONTAINS;

        if (containsBy(other)) return RangeRelation.CONTAINED_BY;

        if (overlaps(other)) {
            if (startsBefore(other)) return RangeRelation.OVERLAPS_BEFORE;
            else return RangeRelation.OVERLAPS_AFTER;
        }

        if (startsBefore(other)) {
            if (immediatelyBefore(other)) return RangeRelation.IMMEDIATELY_BEFORE;
            else return RangeRelation.BEFORE;
        } else {
            if (immediatelyAfter(other)) return RangeRelation.IMMEDIATELY_AFTER;
            else return RangeRelation.AFTER;
        }
    }
}
