package com.seatliberator.seatliberator.kernel.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Kernel Test: Formatted String Generator")
public class FormattedStringGeneratorTest {

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("format이 null이면 예외를 던진다")
        void throw_exception_when_format_is_null() {
            var counter = new SequenceCounter();

            assertThatThrownBy(() -> new FormattedStringGenerator(null, counter))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("format must not be null or blank.");
        }

        @Test
        @DisplayName("format이 비어 있거나 공백이면 예외를 던진다")
        void throw_exception_when_format_is_blank() {
            var counter = new SequenceCounter();

            assertThatThrownBy(() -> new FormattedStringGenerator("", counter))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("format must not be null or blank.");

            assertThatThrownBy(() -> new FormattedStringGenerator("   ", counter))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("format must not be null or blank.");
        }

        @Test
        @DisplayName("counter가 null이면 예외를 던진다")
        void throw_exception_when_counter_is_null() {
            assertThatThrownBy(() -> new FormattedStringGenerator("seat-%s", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("counter must not be null.");
        }

        @Test
        @DisplayName("of 팩토리 메서드로 생성할 수 있다")
        void create_generator_with_of_factory() {
            var generator = FormattedStringGenerator.of("seat-%s", new BijectiveBase26Counter());

            assertThat(generator.generate()).isEqualTo("seat-A");
        }
    }

    @Nested
    @DisplayName("Generate")
    class Generate {
        @Test
        @DisplayName("카운터 값을 포맷 문자열에 적용해 문자열을 생성한다")
        void generate_formatted_string_with_counter_value() {
            var generator = new FormattedStringGenerator("seat-%s", new BijectiveBase26Counter());

            assertThat(generator.generate()).isEqualTo("seat-A");
            assertThat(generator.generate()).isEqualTo("seat-B");
        }

        @Test
        @DisplayName("카운터 원값을 그대로 전달해 타입에 맞는 포맷을 적용한다")
        void apply_format_with_original_counter_value() {
            var generator = new FormattedStringGenerator("seat-%02d", new SequenceCounter());

            assertThat(generator.generate()).isEqualTo("seat-00");
            assertThat(generator.generate()).isEqualTo("seat-01");
        }
    }
}
