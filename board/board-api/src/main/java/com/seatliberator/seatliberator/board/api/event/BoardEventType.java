package com.seatliberator.seatliberator.board.api.event;

import com.seatliberator.seatliberator.eventrelay.core.model.EventType;

public enum BoardEventType implements EventType {
    POST_CREATED,
    POST_DELETED,
    CATEGORY_CREATED,
    CATEGORY_DELETED,
}
