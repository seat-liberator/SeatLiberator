package com.seatliberator.seatliberator.idempotency.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ImmutableIdempotencyContext")
public class ImmutableIdempotencyContextTest {
    @Test
    @DisplayName("fingerprint가 유효하면 생성한다")
    void fingerprint가_유효하면_생성한다() {
        // given
        String fingerprint = "fingerprint-abc-123";

        // when
        ImmutableIdempotencyContext context = new ImmutableIdempotencyContext(fingerprint);

        // then
        assertEquals(fingerprint, context.fingerprint());
    }

    @Test
    @DisplayName("fingerprint가 공백이면 예외를 던진다")
    void fingerprint가_공백이면_예외를_던진다() {
        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ImmutableIdempotencyContext("   ")
        );

        assertEquals("fingerprint must not be blank.", exception.getMessage());
    }

    @Test
    @DisplayName("IdempotencyContext로부터 불변 Context를 복사 생성한다")
    void IdempotencyContext로부터_불변_Context를_복사_생성한다() {
        // given
        IdempotencyContext source = new IdempotencyContext() {
            @Override
            public String fingerprint() {
                return "fingerprint-xyz";
            }
        };

        // when
        ImmutableIdempotencyContext copied = ImmutableIdempotencyContext.of(source);

        // then
        assertEquals("fingerprint-xyz", copied.fingerprint());
    }
}
