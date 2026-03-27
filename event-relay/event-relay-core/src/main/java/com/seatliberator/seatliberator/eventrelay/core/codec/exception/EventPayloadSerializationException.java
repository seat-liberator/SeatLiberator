package com.seatliberator.seatliberator.eventrelay.core.codec.exception;

public class EventPayloadSerializationException extends EventSerializationException {
    private static final String MESSAGE = "Event Payload 직렬화에 실패했습니다.";

    public EventPayloadSerializationException() {
        super(MESSAGE);
    }

    public EventPayloadSerializationException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
