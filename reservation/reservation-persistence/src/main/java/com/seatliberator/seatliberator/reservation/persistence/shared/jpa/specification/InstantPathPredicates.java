package com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification;

import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.Instant;
import java.util.function.Function;

public class InstantPathPredicates {
    public static <T> PredicateSpecification<T> beforeRange(
            InstantRange range,
            Function<From<?, T>, Path<Instant>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.lessThan(path, range.startAt());
        };
    }

    public static <T> PredicateSpecification<T> equalsRangeStart(
            InstantRange range,
            Function<From<?, T>, Path<Instant>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.equal(path, range.startAt());
        };
    }

    public static <T> PredicateSpecification<T> equalsRangeEnd(
            InstantRange range,
            Function<From<?, T>, Path<Instant>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.equal(path, range.endAt());
        };
    }

    public static <T> PredicateSpecification<T> afterRange(
            InstantRange range,
            Function<From<?, T>, Path<Instant>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.greaterThan(path, range.endAt());
        };
    }

    public static <T> PredicateSpecification<T> containedInRange(
            InstantRange range,
            Function<From<?, T>, Path<Instant>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.greaterThanOrEqualTo(path, range.startAt()),
                    cb.lessThan(path, range.endAt())
            );
        };
    }
}
