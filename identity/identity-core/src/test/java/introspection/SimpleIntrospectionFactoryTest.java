package introspection;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.identity.core.introspect.Introspection;
import com.seatliberator.seatliberator.identity.core.introspect.IntrospectionFactory;
import com.seatliberator.seatliberator.identity.core.introspect.SimpleIntrospection;
import com.seatliberator.seatliberator.identity.core.introspect.SimpleIntrospectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleIntrospectionFactoryTest {
    private final IntrospectionFactory factory = new SimpleIntrospectionFactory();

    @Test
    @DisplayName("createNoContent는 비활성 introspection을 생성한다")
    void createNoContent는_비활성_introspection을_생성한다() {
        // when
        Introspection introspection = factory.createNoContent();

        // then
        assertNotNull(introspection);
        assertInstanceOf(SimpleIntrospection.class, introspection);
        assertFalse(introspection.active());
        assertNull(introspection.expiration());
        assertNull(introspection.actor());
    }

    @Test
    @DisplayName("createIntrospection은 활성 introspection을 생성한다")
    void shouldCreateActiveIntrospection() {
        // given
        Actor actor = new SimpleActor("user-1", Set.of("seat:read", "seat:reserve"));
        Long expiration = 999999L;

        // when
        Introspection introspection = factory.createIntrospection(expiration, actor);

        // then
        assertNotNull(introspection);
        assertInstanceOf(SimpleIntrospection.class, introspection);
        assertTrue(introspection.active());
        assertEquals(expiration, introspection.expiration());
        assertEquals(actor, introspection.actor());
    }

    @Test
    @DisplayName("createIntrospection에 null expiration을 전달하면 예외가 발생한다")
    void shouldThrowWhenExpirationIsNull() {
        // given
        Actor actor = new SimpleActor("user-1", Set.of("seat:read"));

        // when & then
        assertThrows(NullPointerException.class, () -> factory.createIntrospection(null, actor));
    }

    @Test
    @DisplayName("createIntrospection에 null actor를 전달하면 예외가 발생한다")
    void shouldThrowWhenActorIsNull() {
        // given
        Long expiration = 999999L;

        // when & then
        assertThrows(NullPointerException.class, () -> factory.createIntrospection(expiration, null));
    }
}
