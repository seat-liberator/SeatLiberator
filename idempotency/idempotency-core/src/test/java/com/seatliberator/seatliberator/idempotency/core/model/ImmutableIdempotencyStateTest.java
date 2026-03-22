package com.seatliberator.seatliberator.idempotency.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ImmutableIdempotencyState")
public class ImmutableIdempotencyStateTest {

    @Test
    @DisplayName("초기화 시 ExecutionState가 Pending 상태로 생성된다")
    void 초기화_시_ExecutionState가_Pending_상태로_생성된다() {
        var sourceKey = "source-key";
        var operationKey = "operation-key";
        var fingerprint = "fingerprint-1asd";
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        ImmutableIdempotencyKey key = new ImmutableIdempotencyKey(sourceKey, operationKey);
        ImmutableIdempotencyContext context = new ImmutableIdempotencyContext(fingerprint);

        ImmutableIdempotencyState state = ImmutableIdempotencyState.create(key, context, createdAt);

        assertAll(
                () -> assertEquals(state.key(), key),
                () -> assertEquals(state.context(), context),
                () -> assertEquals(state.createdAt(), createdAt),
                () -> assertEquals(ExecutionPhase.PENDING, state.executionState().phase())
        );
    }
}
