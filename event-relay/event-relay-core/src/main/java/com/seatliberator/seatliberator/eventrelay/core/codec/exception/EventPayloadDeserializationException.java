package com.seatliberator.seatliberator.eventrelay.core.codec.exception;

public class EventPayloadDeserializationException extends EventSerializationException {
    private static final String MESSAGE = "Event Payload 역직렬화에 실패했습니다.";

    public EventPayloadDeserializationException() {
        super(MESSAGE);
    }

    public EventPayloadDeserializationException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
