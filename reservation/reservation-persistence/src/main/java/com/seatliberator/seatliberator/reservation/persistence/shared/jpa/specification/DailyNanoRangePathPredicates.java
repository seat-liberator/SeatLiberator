package com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification;

import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.EmbeddableDailyNanoRange;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalTime;
import java.util.function.Function;

public class DailyNanoRangePathPredicates {
    public static <T> PredicateSpecification<T> sameRange(
            DailyNanoRange range,
            Function<From<?, T>, Path<EmbeddableDailyNanoRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.equal(path.get("startNanoOfDay"), range.startNanoOfDay()),
                    cb.equal(path.get("endNanoOfDay"), range.endNanoOfDay())
            );
        };
    }

    public static <T> PredicateSpecification<T> containsRange(
            DailyNanoRange range,
            Function<From<?, T>, Path<EmbeddableDailyNanoRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.lessThanOrEqualTo(path.get("startNanoOfDay"), range.startNanoOfDay()),
                    cb.greaterThanOrEqualTo(path.get("endNanoOfDay"), range.endNanoOfDay())
            );
        };
    }

    public static <T> PredicateSpecification<T> containsPoint(
            LocalTime point,
            Function<From<?, T>, Path<EmbeddableDailyNanoRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            var pointNanoOfDay = point.toNanoOfDay();
            return cb.and(
                    cb.lessThanOrEqualTo(path.get("startNanoOfDay"), pointNanoOfDay),
                    cb.greaterThan(path.get("endNanoOfDay"), pointNanoOfDay)
            );
        };
    }

    public static <T> PredicateSpecification<T> containedByRange(
            DailyNanoRange range,
            Function<From<?, T>, Path<EmbeddableDailyNanoRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.greaterThanOrEqualTo(path.get("startNanoOfDay"), range.startNanoOfDay()),
                    cb.lessThanOrEqualTo(path.get("endNanoOfDay"), range.endNanoOfDay())
            );
        };
    }

    public static <T> PredicateSpecification<T> overlapsRange(
            DailyNanoRange range,
            Function<From<?, T>, Path<EmbeddableDailyNanoRange>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.lessThan(path.get("startNanoOfDay"), range.endNanoOfDay()),
                    cb.greaterThan(path.get("endNanoOfDay"), range.startNanoOfDay())
            );
        };
    }
}
