package com.seatliberator.seatliberator.eventrelay.core.codec.exception;

public class EventEnvelopeDeserializationException extends EventSerializationException {
    private static final String MESSAGE = "Event Envelope 역직렬화에 실패했습니다.";

    public EventEnvelopeDeserializationException() {
        super(MESSAGE);
    }

    public EventEnvelopeDeserializationException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
