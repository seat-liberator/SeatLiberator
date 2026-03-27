package com.seatliberator.seatliberator.eventrelay.core.store.exception;

import com.seatliberator.seatliberator.eventrelay.core.store.model.EventState;
import com.seatliberator.seatliberator.eventrelay.core.store.model.EventStatus;
import org.jspecify.annotations.NonNull;

import java.time.Instant;

public class InvalidStateForTransitionException extends EventStateException {
    private static final String DEFAULT_MESSAGE = "이벤트 상태를 %s에서 %s로 전이할 수 없습니다.";

    public InvalidStateForTransitionException(
            @NonNull EventState state,
            @NonNull EventStatus target
    ) {
        super(String.format(DEFAULT_MESSAGE, state.status(), target));
    }

    public InvalidStateForTransitionException(
            @NonNull EventState state,
            @NonNull EventStatus target,
            Throwable cause
    ) {
        super(
                String.format(DEFAULT_MESSAGE, state.status(), target),
                cause
        );
    }

    public InvalidStateForTransitionException(
            @NonNull EventState state,
            @NonNull EventStatus target,
            @NonNull String detail
    ) {
        super(String.format(DEFAULT_MESSAGE + " %s", state.status(), target, detail));
    }

    public InvalidStateForTransitionException(
            @NonNull EventState state,
            @NonNull EventStatus target,
            @NonNull String actualField,
            @NonNull Instant actual,
            @NonNull String expectedField,
            @NonNull Instant expected
    ) {
        super(
                String.format(
                        DEFAULT_MESSAGE + " %s",
                        state.status(),
                        target,
                        buildInvalidTimeMessage(
                                actualField,
                                actual,
                                expectedField,
                                expected
                        )
                )
        );
    }

    private static String buildInvalidTimeMessage(
            @NonNull String actualField,
            @NonNull Instant actual,
            @NonNull String expectField,
            @NonNull Instant expect
    ) {
        return String.format(
                "%s(%s)는 %s(%s)보다 이를 수 없습니다.",
                actualField,
                actual,
                expectField,
                expect
        );
    }
}
