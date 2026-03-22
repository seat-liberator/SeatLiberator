package com.seatliberator.seatliberator.idempotency.core.decision;

import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyContext;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyState;
import org.jspecify.annotations.NonNull;

/**
 * 현재 멱등 상태와 이번 요청의 Context를 바탕으로 실행 여부를 판단한다.
 */
public interface ExecutionDecisionEngine {
    @NonNull ExecutionDecision decide(
            @NonNull IdempotencyState idempotencyState,
            @NonNull IdempotencyContext executionContext
    );
}
