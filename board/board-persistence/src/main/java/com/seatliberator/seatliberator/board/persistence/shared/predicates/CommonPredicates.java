package com.seatliberator.seatliberator.board.persistence.shared.predicates;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.Collection;
import java.util.function.Function;

public class CommonPredicates {
    public static <T, V> PredicateSpecification<T> equals(
            V value,
            Function<From<?, T>, Path<V>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);

            return value == null ? cb.isNull(path) : cb.equal(path, value);
        };
    }

    public static <T> PredicateSpecification<T> like(
            String value,
            Function<From<?, T>, Path<String>> pathFunction
    ) {
        return (from, cb) -> {
            if (value == null || value.isBlank()) return cb.conjunction();

            var path = pathFunction.apply(from);

            String pattern = "%" + value + "%";

            return cb.like(path, pattern);
        };
    }

    public static <T, V> PredicateSpecification<T> isIncluded(
            Collection<V> values,
            Function<From<?, T>, Path<V>> pathFunction
    ) {
        return (from, cb) -> {
            if (values == null || values.isEmpty()) return cb.conjunction();

            return pathFunction.apply(from).in(values);
        };
    }

    public static <T, V> PredicateSpecification<T> isExcluded(
            Collection<V> values,
            Function<From<?, T>, Path<V>> pathFunction
    ) {
        return (from, cb) -> {
            if (values == null || values.isEmpty()) return cb.conjunction();

            return cb.not(pathFunction.apply(from).in(values));
        };
    }
}