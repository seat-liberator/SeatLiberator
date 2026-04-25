package com.seatliberator.seatliberator.reservation.persistence.shared.jpa.specification;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.Collection;
import java.util.function.Function;

public class CommonPredicates {
    public static <T, V> PredicateSpecification<T> excludeIn(
            Collection<V> values,
            Function<From<?, T>, Path<V>> pathFunction
    ) {
        return (from, cb) -> {
            if (values == null || values.isEmpty()) return cb.conjunction();
            return cb.not(pathFunction.apply(from).in(values));
        };
    }

    public static <T, V> PredicateSpecification<T> in(
            Collection<V> values,
            Function<From<?, T>, Path<V>> pathFunction
    ) {
        return (from, cb) -> {
            if (values == null || values.isEmpty()) return cb.conjunction();
            return pathFunction.apply(from).in(values);
        };
    }

    public static <T, V> PredicateSpecification<T> eq(V value, Function<From<?, T>, Path<V>> pathFunction) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);
            return value == null ? cb.isNull(path) : cb.equal(path, value);
        };
    }
}
