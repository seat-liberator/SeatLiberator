package com.seatliberator.seatliberator.idempotency.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImmutableExecutionOutputTest {

    @Test
    @DisplayName("Success는 Result만으로 생성할 수 있다")
    void Success는_Result만으로_생성할_수_있다() {
        Object result = new Object();

        ImmutableExecutionOutput output = ImmutableExecutionOutput.success(result);

        assertAll(
                () -> assertEquals(output.result(), result),
                () -> assertNull(output.error()),
                () -> assertTrue(output.hasResult()),
                () -> assertFalse(output.hasError())
        );
    }

    @Test
    @DisplayName("Failure는 error만으로 생성할 수 있다")
    void Failure는_error만으로_생성할_수_있다() {
        RuntimeException error = new RuntimeException("Bad");

        ImmutableExecutionOutput output = ImmutableExecutionOutput.failure(error);

        assertAll(
                () -> assertEquals(output.error(), error),
                () -> assertNull(output.result()),
                () -> assertTrue(output.hasError()),
                () -> assertFalse(output.hasResult())
        );
    }

    @Test
    @DisplayName("생성자에 Result와 Error 모두 전달하면 예외를 던진다")
    void 생성자에_Result와_Error_모두_전달하면_예외를_던진다() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ImmutableExecutionOutput(new Object(), new RuntimeException())
        );
    }

    @Test
    @DisplayName("생성자에 Result와 Error 모두 전달하지 않으면 예외를 던진다")
    void 생성자에_Result와_Error_모두_전달하지_않으면_예외를_던진다() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ImmutableExecutionOutput(null, null)
        );
    }
}
