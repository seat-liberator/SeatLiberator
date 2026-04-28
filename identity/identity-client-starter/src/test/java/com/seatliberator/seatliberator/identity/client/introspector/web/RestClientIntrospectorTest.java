package com.seatliberator.seatliberator.identity.client.introspector.web;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.role.Capability;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("RestClient Introspector")
class RestClientIntrospectorTest {

    @Test
    @DisplayName("token introspection 요청을 보내고 응답을 Introspection으로 변환한다")
    void introspect() {
        var builder = RestClient.builder()
                .baseUrl("https://identity.example");
        var server = MockRestServiceServer.bindTo(builder).build();
        var introspector = new RestClientIntrospector(builder.build(), "/introspect");

        server.expect(requestTo("https://identity.example/introspect"))
                .andExpect(method(POST))
                .andExpect(content().json("""
                        {"token":"token-1"}
                        """))
                .andRespond(withSuccess("""
                        {
                          "active": true,
                          "expiration": 123,
                          "actor": {
                            "subject": "user-1",
                            "capabilities": [
                              {
                                "scope": "book:create",
                                "description": "create book"
                              }
                            ]
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        var introspection = introspector.introspect("token-1");

        assertThat(introspection.active()).isTrue();
        assertThat(introspection.expiration()).isEqualTo(123);
        assertThat(introspection.actor())
                .extracting(Actor::subject)
                .isEqualTo("user-1");
        assertThat(introspection.actor().capabilities())
                .extracting(Capability::scope)
                .containsExactly("book:create");
        assertThat(introspection.actor().capabilities())
                .extracting(Capability::description)
                .containsExactly("create book");

        server.verify();
    }
}
