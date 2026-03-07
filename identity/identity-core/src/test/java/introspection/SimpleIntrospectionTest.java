package introspection;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.identity.core.introspect.Introspection;
import com.seatliberator.seatliberator.identity.core.introspect.SimpleIntrospection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleIntrospectionTest {
    @Test
    @DisplayName("SimpleIntrospection은 전달된 값을 그대로 반환한다")
    void SimpleIntrospection은_전달된_값을_그대로_반환한다() {
        // given
        Actor actor = new SimpleActor("user-1", Set.of("seat:read"));
        Long expiration = 123456789L;

        // when
        Introspection introspection = new SimpleIntrospection(true, expiration, actor);

        // then
        assertTrue(introspection.active());
        assertEquals(expiration, introspection.expiration());
        assertEquals(actor, introspection.actor());
    }

    @Test
    @DisplayName("SimpleIntrospection은 null expiration과 actor를 가질 수 있다")
    void shouldAllowNullExpirationAndActor() {
        // when
        Introspection introspection = new SimpleIntrospection(false, null, null);

        // then
        assertFalse(introspection.active());
        assertNull(introspection.expiration());
        assertNull(introspection.actor());
    }
}
