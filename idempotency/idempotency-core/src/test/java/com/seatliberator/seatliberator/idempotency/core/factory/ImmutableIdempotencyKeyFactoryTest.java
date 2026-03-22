package com.seatliberator.seatliberator.idempotency.core.factory;

import com.seatliberator.seatliberator.idempotency.core.model.ImmutableIdempotencyKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ImmutableIdempotencyKeyFactory")
public class ImmutableIdempotencyKeyFactoryTest {
    @Test
    @DisplayName("sourceKey와 operation으로 ImmutableIdempotencyKey를 생성한다")
    void sourceKey와_operation으로_ImmutableIdempotencyKey를_생성한다() {
        // given
        ImmutableIdempotencyKeyFactory factory = new ImmutableIdempotencyKeyFactory();

        // when
        ImmutableIdempotencyKey key = factory.create("reservation-request-1", "reservation:create");

        // then
        assertAll(
                () -> assertEquals("reservation-request-1", key.sourceKey()),
                () -> assertEquals("reservation:create", key.operation())
        );
    }
}
