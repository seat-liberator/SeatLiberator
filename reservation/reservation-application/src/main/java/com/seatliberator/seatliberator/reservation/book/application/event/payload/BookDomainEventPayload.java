package com.seatliberator.seatliberator.reservation.book.application.event.payload;

import java.time.Instant;

public interface BookDomainEventPayload {
    Instant createdAt();
}
