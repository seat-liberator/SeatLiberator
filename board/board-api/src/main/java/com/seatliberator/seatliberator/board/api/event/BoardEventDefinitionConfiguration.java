package com.seatliberator.seatliberator.board.api.event;

import com.seatliberator.seatliberator.board.api.BoardApi;
import com.seatliberator.seatliberator.board.api.event.payload.CategoryCreatedEventPayload;
import com.seatliberator.seatliberator.board.api.event.payload.CategoryDeletedEventPayload;
import com.seatliberator.seatliberator.board.api.event.payload.PostCreatedEventPayload;
import com.seatliberator.seatliberator.board.api.event.payload.PostDeletedEventPayload;
import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinitionCollection;
import com.seatliberator.seatliberator.eventrelay.core.definition.SimpleEventDefinition;
import com.seatliberator.seatliberator.eventrelay.core.definition.SimpleEventDefinitionCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BoardEventDefinitionConfiguration {
    @Bean
    EventDefinitionCollection boardEventDefinitionCollection() {
        return new SimpleEventDefinitionCollection(
                BoardApi.NAMESPACE,
                List.of(
                        new SimpleEventDefinition<>(BoardEventType.POST_CREATED, PostCreatedEventPayload.class),
                        new SimpleEventDefinition<>(BoardEventType.POST_DELETED, PostDeletedEventPayload.class),
                        new SimpleEventDefinition<>(BoardEventType.CATEGORY_CREATED, CategoryCreatedEventPayload.class),
                        new SimpleEventDefinition<>(BoardEventType.CATEGORY_DELETED, CategoryDeletedEventPayload.class)
                )
        );
    }
}
