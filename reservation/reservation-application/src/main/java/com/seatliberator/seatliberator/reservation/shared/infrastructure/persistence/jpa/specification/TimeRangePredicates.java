package com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification;

import com.seatliberator.seatliberator.reservation.domain.EmbeddableTimeRange;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.Instant;
import java.util.function.Function;

public class TimeRangePredicates {
    public static <T> PredicateSpecification<T> eq(
            TimeRange range,
            Function<From<?, T>, Path<EmbeddableTimeRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.equal(path.get("startAt"), range.startAt()),
                    cb.equal(path.get("endAt"), range.endAt())
            );
        };
    }

    public static <T> PredicateSpecification<T> overlap(
            TimeRange range,
            Function<From<?, T>, Path<EmbeddableTimeRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.lessThan(path.get("startAt"), range.endAt()),
                    cb.greaterThan(path.get("endAt"), range.startAt())
            );
        };
    }

    public static <T> PredicateSpecification<T> containedBy(
            TimeRange range,
            Function<From<?, T>, Path<EmbeddableTimeRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.greaterThanOrEqualTo(path.get("startAt"), range.startAt()),
                    cb.lessThanOrEqualTo(path.get("endAt"), range.endAt())
            );
        };
    }

    public static <T> PredicateSpecification<T> contains(
            TimeRange range,
            Function<From<?, T>, Path<EmbeddableTimeRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.lessThanOrEqualTo(path.get("startAt"), range.startAt()),
                    cb.greaterThanOrEqualTo(path.get("endAt"), range.endAt())
            );
        };
    }

    public static <T> PredicateSpecification<T> contain(
            Instant at,
            Function<From<?, T>, Path<EmbeddableTimeRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.lessThanOrEqualTo(path.get("startAt"), at),
                    cb.greaterThanOrEqualTo(path.get("endAt"), at)
            );
        };
    }

    public static <T> Function<From<?, T>, Path<EmbeddableTimeRange>> defaultRangePathFunction() {
        return from -> from.get("range");
    }
}
