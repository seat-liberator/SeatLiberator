package com.seatliberator.seatliberator.kernel.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Kernel Test: Bijective Base26 Counter")
public class BijectiveBase26CounterTest {

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("기본 생성자로 생성하면 첫 번째 카운트는 A를 반환한다")
        void return_a_as_first_count_when_created_without_initial_value() {
            var counter = new BijectiveBase26Counter();

            assertThat(counter.next()).isEqualTo("A");
        }

        @Test
        @DisplayName("초기 값을 전달해서 생성하면 해당 순서의 문자열부터 시작한다")
        void start_from_initial_value_when_created_with_initial_value() {
            var counter = new BijectiveBase26Counter(26);

            assertThat(counter.next()).isEqualTo("AA");
        }

        @Test
        @DisplayName("음수 초기 값을 전달하면 예외를 던진다")
        void throw_exception_when_initial_value_is_negative() {
            assertThatThrownBy(() -> new BijectiveBase26Counter(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("initialValue must not be negative.");
        }

        @Test
        @DisplayName("String 타입 카운터를 지원한다")
        void support_string_type() {
            var counter = new BijectiveBase26Counter();

            assertThat(counter.support()).isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("Count")
    class Count {
        @Test
        @DisplayName("Z 다음에는 AA와 AB를 순서대로 반환한다")
        void continue_to_aa_and_ab_after_z() {
            var counter = new BijectiveBase26Counter();

            assertThat(advance(counter, 26)).isEqualTo("Z");
            assertThat(counter.next()).isEqualTo("AA");
            assertThat(counter.next()).isEqualTo("AB");
        }

        @Test
        @DisplayName("자릿수가 바뀌어도 bijective base26 순서를 유지한다")
        void keep_bijective_base26_order_when_digit_changes() {
            var counter = new BijectiveBase26Counter();

            assertThat(advance(counter, 52)).isEqualTo("AZ");
            assertThat(counter.next()).isEqualTo("BA");
            assertThat(advance(counter, 649)).isEqualTo("ZZ");
            assertThat(counter.next()).isEqualTo("AAA");
        }

        @Test
        @DisplayName("초기 값을 기준으로 다음 문자열을 연속해서 반환한다")
        void return_sequential_values_from_initial_value() {
            var counter = new BijectiveBase26Counter(26);

            assertThat(counter.next()).isEqualTo("AA");
            assertThat(counter.next()).isEqualTo("AB");
            assertThat(counter.next()).isEqualTo("AC");
        }
    }

    private String advance(BijectiveBase26Counter counter, int count) {
        String value = null;
        for (int i = 0; i < count; i++) {
            value = counter.next();
        }
        return value;
    }
}
