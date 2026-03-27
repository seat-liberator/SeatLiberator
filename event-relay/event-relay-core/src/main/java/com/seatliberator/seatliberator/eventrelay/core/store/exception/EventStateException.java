package com.seatliberator.seatliberator.eventrelay.core.store.exception;

public class EventStateException extends RuntimeException {
    public EventStateException(String message) {
        super(message);
    }

    public EventStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
