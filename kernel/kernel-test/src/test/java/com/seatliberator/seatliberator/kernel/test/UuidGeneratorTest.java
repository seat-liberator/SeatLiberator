package com.seatliberator.seatliberator.kernel.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Kernel Test: UUID Generator")
public class UuidGeneratorTest {

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("counter가 null이면 예외를 던진다")
        void throw_exception_when_counter_is_null() {
            assertThatThrownBy(() -> new UuidGenerator(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("counter must not be null.");
        }
    }

    @Nested
    @DisplayName("Generate")
    class Generate {
        @Test
        @DisplayName("카운터 값을 UUID 하위 비트에 사용한다")
        void generate_uuid_with_counter_sequence() {
            var generator = new UuidGenerator(new SequenceCounter());

            assertThat(generator.generate()).isEqualTo(new UUID(0L, 0L));
            assertThat(generator.generate()).isEqualTo(new UUID(0L, 1L));
            assertThat(generator.generate()).isEqualTo(new UUID(0L, 2L));
        }
    }
}
