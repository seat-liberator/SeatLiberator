package com.seatliberator.seatliberator.idempotency.core.decision;

/**
 * 실행 판단 결과 종류
 */
public enum Decision {
    EXECUTE,

    REUSE_RUNNING_STATE,
    REUSE_RESOLVED_RESULT,

    REJECT_CONTEXT_MISMATCH,
    REJECT_ATTEMPT_LIMIT_EXCEEDED,
    REJECT_EXECUTION_TIMEOUT
}
