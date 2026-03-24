package com.seatliberator.seatliberator.idempotency.core.processor;

import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyContext;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyKey;
import org.jspecify.annotations.NonNull;

/**
 * 최상위 오케스트레이터
 * <p>
 * 조회 -> 판단 -> 결과 복원 및 실행 흐름 관리를 담당한다.
 */
public interface IdempotentProcessor {
    <T> T process(
            @NonNull IdempotencyKey key,
            @NonNull IdempotencyContext context,
            @NonNull IdempotencyAction<T> action
    );
}
