package com.seatliberator.seatliberator.eventrelay.core.store.exception;

public class EventNotFoundException extends EventStoreException {
    public EventNotFoundException(String eventId) {
        super(String.format("이벤트를 찾을 수 없습니다. id=%s", eventId));
    }
}
