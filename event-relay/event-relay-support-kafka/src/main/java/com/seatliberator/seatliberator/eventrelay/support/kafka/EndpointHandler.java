package com.seatliberator.seatliberator.eventrelay.support.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jspecify.annotations.NonNull;

public interface EndpointHandler {
    void onMessage(@NonNull ConsumerRecord<String, String> record);
}
