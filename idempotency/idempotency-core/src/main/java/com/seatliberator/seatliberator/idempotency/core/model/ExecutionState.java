package com.seatliberator.seatliberator.idempotency.core.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface ExecutionState {
    /**
     * 현재 멱등 관리 대상 메서드의 실행 상태
     *
     * @return ExecutionPhase
     */
    @NonNull ExecutionPhase phase();

    /**
     * 지금까지 실행 시도 횟수<br>
     * - PENDING: 0<br>
     * - 첫 실행 시작 후 RUNNING: 1<br>
     * - RUNNING -> RESOLVED / EXECUTION_ERROR 전이 시에는 증가하지 않음,<br>
     * - EXECUTION_ERROR 이후 재실행으로 RUNNING 전이 시 1 증가
     *
     */
    int attemptCount();

    /**
     * 실제 비즈니스 로직 실행을 시작한 시간<br>
     * present if phase is ExecutionPhase.{RUNNING, RESOLVED, EXECUTION_ERROR}
     */
    @Nullable Instant tryStartAt();

    /**
     * 비즈니스 로직 호출 후 종료된 시간<br>
     * present if phase is ExecutionPhase.RESOLVED
     */
    @Nullable Instant resolvedAt();

    /**
     * 비즈니스 로직 호출이 종료된 후 최종 결과<br>
     * 반환 객체(true positive) 또는 로직에서 던진 예외(true negative)를 담는다.<br>
     * present if phase is ExecutionPhase.RESOLVED
     */
    @Nullable ExecutionOutput output();

    default boolean isPending() {
        return phase() == ExecutionPhase.PENDING;
    }

    default boolean isRunning() {
        return phase() == ExecutionPhase.RUNNING;
    }

    default boolean isResolved() {
        return phase() == ExecutionPhase.RESOLVED;
    }

    /**
     * 비즈니스 로직 실행 도중 비정상 중단 오류 표현<br>
     * 네트워크 실패 및 프로세스의 비정상 종료 등으로 인해 실행이 중단됐으므로, 재시도 시 성공할 가능성이 있음<br>
     */
    default boolean isExecutionError() {
        return phase() == ExecutionPhase.EXECUTION_ERROR;
    }
}
