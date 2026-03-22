package com.seatliberator.seatliberator.idempotency.core.factory;

import com.seatliberator.seatliberator.idempotency.core.model.ImmutableIdempotencyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ImmutableIdempotencyContextFactory")
public class ImmutableIdempotencyContextFactoryTest {
    @Test
    @DisplayName("fingerprint로 ImmutableIdempotencyContext를 생성한다")
    void fingerprint로_ImmutableIdempotencyContext를_생성한다() {
        // given
        ImmutableIdempotencyContextFactory factory = new ImmutableIdempotencyContextFactory();

        // when
        ImmutableIdempotencyContext context = factory.create("fingerprint-abc-123");

        // then
        assertEquals("fingerprint-abc-123", context.fingerprint());
    }
}
