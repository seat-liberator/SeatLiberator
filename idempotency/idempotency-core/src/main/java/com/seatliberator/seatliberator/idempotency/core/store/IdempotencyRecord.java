package com.seatliberator.seatliberator.idempotency.core.store;

import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyState;
import org.jspecify.annotations.NonNull;

/**
 * 저장소 조회/생성 결과
 *
 * @param state
 * @param newlyCreated: 이번 getOrCreate 호출에서 새로 생성되었는지 여부
 */
public record IdempotencyRecord(
        @NonNull IdempotencyState state,
        boolean newlyCreated
) {
}
