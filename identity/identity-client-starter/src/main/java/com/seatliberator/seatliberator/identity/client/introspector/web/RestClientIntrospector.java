package com.seatliberator.seatliberator.identity.client.introspector.web;

import com.seatliberator.seatliberator.identity.core.introspect.Introspection;
import com.seatliberator.seatliberator.identity.core.introspect.Introspector;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class RestClientIntrospector implements Introspector {
    private final RestClient restClient;
    private final String apiUrl;

    public RestClientIntrospector(
            RestClient restClient,
            String apiUrl
    ) {
        this.restClient = restClient;
        this.apiUrl = apiUrl;
    }

    @Override
    public Introspection introspect(String token) {
        var payload = Map.of("token", token);

        return restClient.post()
                .uri(apiUrl)
                .body(payload)
                .retrieve()
                .body(WebIntrospectionResponse.class);
    }
}
