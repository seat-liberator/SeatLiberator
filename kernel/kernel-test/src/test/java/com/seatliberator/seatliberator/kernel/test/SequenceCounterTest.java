package com.seatliberator.seatliberator.kernel.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Kernel Test: Sequence Counter")
public class SequenceCounterTest {

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("기본 생성자로 생성하면 0부터 시작한다")
        void start_count_from_zero_when_created_without_initial_value() {
            var counter = new SequenceCounter();

            assertThat(counter.next()).isZero();
        }

        @Test
        @DisplayName("초기 값을 전달해서 생성하면 해당 값부터 시작한다")
        void start_count_from_initial_value_when_created_with_initial_value() {
            var counter = new SequenceCounter(10);

            assertThat(counter.next()).isEqualTo(10);
        }

        @Test
        @DisplayName("Integer 타입 카운터를 지원한다")
        void support_integer_type() {
            var counter = new SequenceCounter();

            assertThat(counter.support()).isEqualTo(Integer.class);
        }
    }

    @Nested
    @DisplayName("Count")
    class Count {
        @Test
        @DisplayName("호출할 때마다 1씩 증가한다")
        void increase_count_by_one_for_each_call() {
            var counter = new SequenceCounter();

            assertThat(counter.next()).isZero();
            assertThat(counter.next()).isEqualTo(1);
            assertThat(counter.next()).isEqualTo(2);
        }

        @Test
        @DisplayName("초기 값을 기준으로 연속된 값을 반환한다")
        void return_sequential_values_from_initial_value() {
            var counter = new SequenceCounter(10);

            assertThat(counter.next()).isEqualTo(10);
            assertThat(counter.next()).isEqualTo(11);
            assertThat(counter.next()).isEqualTo(12);
        }
    }
}
