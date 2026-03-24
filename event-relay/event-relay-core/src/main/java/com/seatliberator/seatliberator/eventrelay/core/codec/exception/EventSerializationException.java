package com.seatliberator.seatliberator.eventrelay.core.codec.exception;

public class EventSerializationException extends RuntimeException {
    public EventSerializationException(String message) {
        super(message);
    }

    public EventSerializationException(String message, Throwable causes) {
        super(message, causes);
    }
}
