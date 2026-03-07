package introspection;

import com.seatliberator.seatliberator.identity.core.actor.ActorFactory;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActorFactory;
import com.seatliberator.seatliberator.identity.core.introspect.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BypassIntrospectionTest {
    private final ActorFactory actorFactory = new SimpleActorFactory();
    private final IntrospectionFactory introspectionFactory = new SimpleIntrospectionFactory();

    @Test
    @DisplayName("BypassIntrospector는 항상 active introspection을 반환한다")
    void shouldReturnActiveIntrospection() {
        // given
        Long expiration = 60000L;
        Introspector introspector = new BypassIntrospector(
                actorFactory,
                introspectionFactory,
                expiration
        );

        // when
        Introspection introspection = introspector.introspect("any-token");

        // then
        assertNotNull(introspection);
        assertTrue(introspection.active());
        assertEquals(expiration, introspection.expiration());
        assertNotNull(introspection.actor());
    }

    @Test
    @DisplayName("BypassIntrospector는 호출마다 서로 다른 fake subject를 생성한다")
    void shouldCreateDifferentFakeSubjectPerInvocation() {
        // given
        Introspector introspector = new BypassIntrospector(
                actorFactory,
                introspectionFactory,
                60000L
        );

        // when
        Introspection first = introspector.introspect("token-1");
        Introspection second = introspector.introspect("token-2");

        // then
        assertNotNull(first.actor());
        assertNotNull(second.actor());
        assertNotEquals(first.actor().subject(), second.actor().subject());
    }

    @Test
    @DisplayName("BypassIntrospector는 전달받은 token 값과 무관하게 introspection을 반환한다")
    void shouldIgnoreTokenValue() {
        // given
        Long expiration = 12345L;
        Introspector introspector = new BypassIntrospector(
                actorFactory,
                introspectionFactory,
                expiration
        );

        // when
        Introspection introspection1 = introspector.introspect("token-a");
        Introspection introspection2 = introspector.introspect("token-b");

        // then
        assertTrue(introspection1.active());
        assertTrue(introspection2.active());
        assertEquals(expiration, introspection1.expiration());
        assertEquals(expiration, introspection2.expiration());
    }
}
