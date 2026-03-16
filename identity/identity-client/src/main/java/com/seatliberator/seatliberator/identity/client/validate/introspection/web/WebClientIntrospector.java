package com.seatliberator.seatliberator.identity.client.validate.introspection.web;

import com.seatliberator.seatliberator.identity.core.introspect.Introspection;
import com.seatliberator.seatliberator.identity.core.introspect.Introspector;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class WebClientIntrospector implements Introspector {
    private final WebClient webClient;
    private final String apiUrl;

    public WebClientIntrospector(
            WebClient webClient,
            String apiUrl
    ) {
        this.webClient = webClient;
        this.apiUrl = apiUrl;
    }

    @Override
    public Introspection introspect(String token) {
        var payload = Map.of("token", token);

        return webClient.post()
                .uri(apiUrl)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(WebIntrospectionResponse.class)
                .block();
    }
}
