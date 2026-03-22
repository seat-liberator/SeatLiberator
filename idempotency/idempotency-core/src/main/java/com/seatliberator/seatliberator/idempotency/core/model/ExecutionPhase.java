package com.seatliberator.seatliberator.idempotency.core.model;

public enum ExecutionPhase {
    PENDING,
    RUNNING,
    RESOLVED,
    EXECUTION_ERROR
}
