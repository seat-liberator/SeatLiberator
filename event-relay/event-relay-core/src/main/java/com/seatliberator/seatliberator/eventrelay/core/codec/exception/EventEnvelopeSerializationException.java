package com.seatliberator.seatliberator.eventrelay.core.codec.exception;

public class EventEnvelopeSerializationException extends EventSerializationException {
    private static final String MESSAGE = "Event Envelope 직렬화에 실패했습니다.";

    public EventEnvelopeSerializationException() {
        super(MESSAGE);
    }

    public EventEnvelopeSerializationException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
