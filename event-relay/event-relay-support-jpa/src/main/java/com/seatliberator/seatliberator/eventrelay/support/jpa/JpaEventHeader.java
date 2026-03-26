package com.seatliberator.seatliberator.eventrelay.support.jpa;

import com.seatliberator.seatliberator.eventrelay.core.model.EventHeader;
import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import com.seatliberator.seatliberator.eventrelay.core.model.ImmutableEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import org.jspecify.annotations.NonNull;

@Embeddable
public class JpaEventHeader implements EventHeader {
    @Transient
    private EventType type;

    @Column(name = "event_type", nullable = false)
    private String typeName;

    public JpaEventHeader() {
    }

    public JpaEventHeader(String typeName) {
        this.typeName = typeName;
        this.type = ImmutableEventType.from(typeName);
    }

    public static JpaEventHeader copyOf(EventHeader header) {
        return new JpaEventHeader(header.eventType().name());
    }

    public static JpaEventHeader from(String typeName) {
        return new JpaEventHeader(typeName);
    }

    @Override
    public @NonNull EventType eventType() {
        if (type == null) {
            type = ImmutableEventType.from(typeName);
        }
        return type;
    }
}
