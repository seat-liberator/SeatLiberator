package com.seatliberator.seatliberator.eventrelay.core.relay;

public record EventRelayOptions(
        int batchSize
) {
    public EventRelayOptions {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }
    }
}
