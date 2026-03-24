package com.seatliberator.seatliberator.eventrelay.core.store.exception;

public class EventStoreException extends RuntimeException {
    public EventStoreException(String message) {
        super(message);
    }
}
