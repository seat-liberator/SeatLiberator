package com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification;

import com.seatliberator.seatliberator.reservation.domain.shared.temporal.EmbeddableInstantRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRange;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.Instant;
import java.util.function.Function;

public class InstantRangePathPredicates {
    public static <T> PredicateSpecification<T> sameRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.equal(path.get("startAt"), range.startAt()),
                    cb.equal(path.get("endAt"), range.endAt())
            );
        };
    }

    public static <T> PredicateSpecification<T> beforeRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.lessThan(path.get("endAt"), range.startAt());
        };
    }

    public static <T> PredicateSpecification<T> beforeAtPoint(
            Instant point,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.lessThan(path.get("endAt"), point);
        };
    }

    public static <T> PredicateSpecification<T> immediatelyBeforeRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.equal(path.get("endAt"), range.startAt());
        };
    }

    public static <T> PredicateSpecification<T> endsAtPoint(
            Instant point,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.equal(path.get("endAt"), point);
        };
    }

    public static <T> PredicateSpecification<T> startBeforeRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.lessThan(path.get("startAt"), range.startAt());
        };
    }

    public static <T> PredicateSpecification<T> endAfterRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.greaterThan(path.get("endAt"), range.endAt());
        };
    }

    public static <T> PredicateSpecification<T> immediatelyAfterRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.equal(path.get("startAt"), range.endAt());
        };
    }

    public static <T> PredicateSpecification<T> startAtPoint(
            Instant point,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.equal(path.get("startAt"), point);
        };
    }

    public static <T> PredicateSpecification<T> afterRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.greaterThan(path.get("startAt"), range.endAt());
        };
    }

    public static <T> PredicateSpecification<T> afterAtPoint(
            Instant point,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.greaterThan(path.get("startAt"), point);
        };
    }

    public static <T> PredicateSpecification<T> containsRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.lessThanOrEqualTo(path.get("startAt"), range.startAt()),
                    cb.greaterThanOrEqualTo(path.get("endAt"), range.endAt())
            );
        };
    }

    public static <T> PredicateSpecification<T> containsPoint(
            Instant point,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.lessThanOrEqualTo(path.get("startAt"), point),
                    cb.greaterThan(path.get("endAt"), point)
            );
        };
    }

    public static <T> PredicateSpecification<T> containedByRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.greaterThanOrEqualTo(path.get("startAt"), range.startAt()),
                    cb.lessThanOrEqualTo(path.get("endAt"), range.endAt())
            );
        };
    }

    public static <T> PredicateSpecification<T> overlapsRange(
            InstantRange range,
            Function<From<?, T>, Path<EmbeddableInstantRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.lessThan(path.get("startAt"), range.endAt()),
                    cb.greaterThan(path.get("endAt"), range.startAt())
            );
        };
    }
}
