package com.seatliberator.seatliberator.eventrelay.core.store.model;

import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;

public interface StoredEvent extends EventEnvelope, EventFlowAware, EventState {
}
