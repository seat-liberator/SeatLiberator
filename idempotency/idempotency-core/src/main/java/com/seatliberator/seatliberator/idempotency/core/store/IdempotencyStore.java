package com.seatliberator.seatliberator.idempotency.core.store;

import com.seatliberator.seatliberator.idempotency.core.model.ExecutionOutput;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyContext;
import com.seatliberator.seatliberator.idempotency.core.model.IdempotencyKey;
import org.jspecify.annotations.NonNull;

/**
 * 멱등 상태 저장소<br>
 * 구현체는 내부적으로 캐시 사용해도 되지만, 캐시는 코어 공개 스펙에 노출하지 않는다.<br>
 * getOrCreate는 신규 생성 시 initialContext를 함께 기록해야 한다.<br>
 * 그래야 PENDING 상태에서도 context 기반 멱등 판단이 가능하다.
 */
public interface IdempotencyStore {
    @NonNull IdempotencyRecord getOrCreate(
            @NonNull IdempotencyKey key,
            @NonNull IdempotencyContext initialContext
    );

    /**
     * PENDING 또는 EXECUTION_ERROR -> RUNNING 전이 시도<br>
     * 성공 시 true, 전이 불가/경쟁 패배 시 false.<br>
     *
     * @param key: 멱등키
     * @return 성공 시 true, 전이 불가 및 경쟁 패배 시 false
     */
    boolean tryMarkRunning(@NonNull IdempotencyKey key);

    /**
     * RUNNING -> RESOLVED 전이를 시도한다.<br>
     *
     * @param key:    멱등키
     * @param output: 실행 결과
     * @return 성공 시 true, 전이 불가 시 false
     */
    boolean tryMarkResolved(
            @NonNull IdempotencyKey key,
            @NonNull ExecutionOutput output
    );

    /**
     * RUNNING -> EXECUTION_ERROR 전이를 시도한다.<br>
     *
     * @param key: 멱등키
     * @return 성공 시 true, 전이 불가 시 false
     */
    boolean tryMarkExecutionError(@NonNull IdempotencyKey key);
}
