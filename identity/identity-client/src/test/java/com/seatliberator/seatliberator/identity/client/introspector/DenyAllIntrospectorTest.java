package com.seatliberator.seatliberator.identity.client.introspection;

import com.seatliberator.seatliberator.identity.client.introspector.DenyAllIntrospector;
import com.seatliberator.seatliberator.identity.core.introspection.IntrospectionFactory;
import com.seatliberator.seatliberator.identity.client.introspector.Introspector;
import com.seatliberator.seatliberator.identity.core.introspection.SimpleIntrospectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DenyAllIntrospectorTest {
    private final String TOKEN = "any-token";
    private final IntrospectionFactory introspectionFactory = new SimpleIntrospectionFactory();

    @Test
    @DisplayName("DenyAllIntrospector는 항상 inactive introspection을 반환한다")
    void shouldReturnInactiveIntrospection() {
        // given
        Introspector introspector = new DenyAllIntrospector(
                introspectionFactory
        );

        com.seatliberator.seatliberator.identity.core.introspection.Introspection introspection = introspector.introspect(TOKEN);

        assertNotNull(introspection);
        assertFalse(introspection.active());
        assertNull(introspection.actor());
        assertNull(introspection.expiration());
    }

    @Test
    @DisplayName("DenyAllIntrospector는 전달받은 token 값과 무관하게 introspection을 반환한다")
    void shouldIgnoreTokenValue() {
        // given
        Introspector introspector = new DenyAllIntrospector(
                introspectionFactory
        );

        com.seatliberator.seatliberator.identity.core.introspection.Introspection introspection1 = introspector.introspect(TOKEN);
        com.seatliberator.seatliberator.identity.core.introspection.Introspection introspection2 = introspector.introspect(TOKEN + "-1");

        assertFalse(introspection1.active());
        assertFalse(introspection2.active());
    }
}
