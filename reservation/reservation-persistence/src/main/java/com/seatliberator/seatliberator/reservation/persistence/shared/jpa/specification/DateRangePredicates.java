package com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification;

import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DateRange;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDate;
import java.util.function.Function;

public class DateRangePredicates {
    public static <T> PredicateSpecification<T> contains(
            DateRange range,
            Function<From<?, T>, Path<LocalDate>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return cb.and(
                    cb.greaterThanOrEqualTo(path, range.startAt()),
                    cb.lessThanOrEqualTo(path, range.endAt())
            );
        };
    }
}
