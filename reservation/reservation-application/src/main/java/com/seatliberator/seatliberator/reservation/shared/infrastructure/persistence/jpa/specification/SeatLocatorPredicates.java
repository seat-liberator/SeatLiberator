package com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification;

import com.seatliberator.seatliberator.reservation.domain.EmbeddableSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.function.Function;

public class SeatLocatorPredicates {
    public static <T> PredicateSpecification<T> eq(
            SeatLocator locator,
            Function<From<?, T>, Path<EmbeddableSeatLocator>> pathFunction
    ) {
        return (from, cb) -> {
            var path = pathFunction.apply(from);
            return cb.and(
                    cb.equal(path.get("roomId"), locator.roomId()),
                    cb.equal(path.get("seatId"), locator.seatId())
            );
        };
    }

    public static <T> PredicateSpecification<T> eqRoomId(
            String roomId,
            Function<From<?, T>, Path<EmbeddableSeatLocator>> pathFunction
    ) {
        return (root, cb) -> {
            var path = pathFunction.apply(root);
            return cb.equal(path.get("roomId"), roomId);
        };
    }

    public static <T> Function<From<?, T>, Path<EmbeddableSeatLocator>> defaultLocatorPathFunction() {
        return from -> from.get("locator");
    }
}
