package com.seatliberator.seatliberator.identity.server.application.role.port.in.query;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FindUserGrantedSummaryQuery(
        UUID userId
) {
    public FindUserGrantedSummaryQuery {
        Preconditions.requireNonNull(userId, "userId");
    }

    public static FindUserGrantedSummaryQuery of(UUID userId) {
        return new FindUserGrantedSummaryQuery(userId);
    }
}
