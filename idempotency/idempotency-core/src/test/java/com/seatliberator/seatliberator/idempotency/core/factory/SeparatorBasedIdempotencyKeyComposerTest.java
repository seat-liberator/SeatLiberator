package com.seatliberator.seatliberator.idempotency.core.factory;

import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyKey;
import com.seatliberator.seatliberator.idempotency.core.model.ImmutableIdempotencyKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SeparatorBasedIdempotencyKeyComposer")
public class SeparatorBasedIdempotencyKeyComposerTest {
    @Test
    @DisplayName("separator가 유효하면 composer를 생성한다")
    void separator가_유효하면_composer를_생성한다() {
        // when & then
        assertDoesNotThrow(() -> new SeparatorBasedIdempotencyKeyComposer(":"));
    }

    @Test
    @DisplayName("separator가 공백이면 예외를 던진다")
    void separator가_공백이면_예외를_던진다() {
        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SeparatorBasedIdempotencyKeyComposer("   ")
        );

        assertEquals("separator must be not blank.", exception.getMessage());
    }

    @Test
    @DisplayName("separator를 사용해 sourceKey와 operation을 조합한다")
    void separator를_사용해_sourceKey와_operation을_조합한다() {
        // given
        SeparatorBasedIdempotencyKeyComposer composer = new SeparatorBasedIdempotencyKeyComposer(":");
        IdempotencyKey key = new ImmutableIdempotencyKey("reservation-request-1", "reservation-create");

        // when
        String composed = composer.compose(key);

        // then
        assertEquals("reservation-request-1:reservation-create", composed);
    }

    @Test
    @DisplayName("sourceKey에 separator가 포함되면 예외를 던진다")
    void sourceKey에_separator가_포함되면_예외를_던진다() {
        // given
        SeparatorBasedIdempotencyKeyComposer composer = new SeparatorBasedIdempotencyKeyComposer(":");
        IdempotencyKey key = new ImmutableIdempotencyKey("reservation:request:1", "reservation-create");

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> composer.compose(key)
        );

        assertEquals("sourceKey must not contain separator.", exception.getMessage());
    }

    @Test
    @DisplayName("operation에 separator가 포함되면 예외를 던진다")
    void operation에_separator가_포함되면_예외를_던진다() {
        // given
        SeparatorBasedIdempotencyKeyComposer composer = new SeparatorBasedIdempotencyKeyComposer(":");
        IdempotencyKey key = new ImmutableIdempotencyKey("reservation-request-1", "reservation:create");

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> composer.compose(key)
        );

        assertEquals("operation must not contain separator.", exception.getMessage());
    }
}
