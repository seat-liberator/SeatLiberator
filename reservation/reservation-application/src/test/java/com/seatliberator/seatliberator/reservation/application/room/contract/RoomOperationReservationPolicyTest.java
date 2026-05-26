package com.seatliberator.seatliberator.reservation.application.room.contract;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicy;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.InstantRangeFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailySchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RoomOperationReservationPolicy 테스트")
public class RoomOperationReservationPolicyTest {
    RoomOperationReservationPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new RoomOperationReservationPolicy(fixedClock);
    }

    @Test
    @DisplayName("방 운영 상태가 CLOSE이면 거절한다")
    void reject_when_room_operation_is_closed() {
        var operationPolicy = new RoomOperationPolicyFixture.Builder()
                .operationStatus(RoomOperationStatus.CLOSE)
                .build();

        var result = policy.evaluate(
                operationPolicy,
                InstantRangeFixture.get("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z")
        );

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.ROOM_OPERATION_CLOSED);
    }

    @Test
    @DisplayName("최대 예약 가능 시간을 초과하면 거절한다")
    void reject_when_reservation_duration_exceeds_limit() {
        var operationPolicy = new RoomOperationPolicyFixture.Builder()
                .maxReservationDuration(Duration.ofMinutes(30))
                .operationStatus(RoomOperationStatus.OPEN)
                .build();

        var result = policy.evaluate(
                operationPolicy,
                InstantRangeFixture.get("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z")
        );

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.MAX_RESERVATION_DURATION_EXCEEDED);
    }

    @Test
    @DisplayName("예약 시간이 운영 시간 구간 안에 있으면 허용한다")
    void accept_when_reservation_range_is_within_operation_schedule() {
        var operationPolicy = openPolicy();

        var result = policy.evaluate(
                operationPolicy,
                InstantRangeFixture.get("2026-01-01T09:00:00Z", "2026-01-01T10:00:00Z")
        );

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.ROOM_OPERATION_AVAILABLE);
    }

    @Test
    @DisplayName("예약 시작 시간이 운영 시간 구간 밖이면 거절한다")
    void reject_when_reservation_start_is_out_of_operation_schedule() {
        var operationPolicy = openPolicy();

        var result = policy.evaluate(
                operationPolicy,
                InstantRangeFixture.get("2026-01-01T07:00:00Z", "2026-01-01T08:00:00Z")
        );

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
    }

    @Test
    @DisplayName("예약 시간이 운영 시간 구간 사이의 공백을 걸치면 거절한다")
    void reject_when_reservation_range_spans_gap_between_operation_schedule() {
        var operationPolicy = openPolicy();

        var result = policy.evaluate(
                operationPolicy,
                InstantRangeFixture.get("2026-01-01T11:30:00Z", "2026-01-01T12:30:00Z")
        );

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
    }

    private RoomOperationPolicy openPolicy() {
        return new RoomOperationPolicyFixture.Builder()
                .maxReservationDuration(Duration.ofHours(2))
                .operationStatus(RoomOperationStatus.OPEN)
                .operationSchedule(operationTimeRange())
                .build();
    }

    private SimpleDailySchedule operationTimeRange() {
        return SimpleDailySchedule.of(List.of(
                range(LocalTime.of(8, 0), Duration.ofHours(4)),
                range(LocalTime.of(13, 0), Duration.ofHours(5))
        ));
    }

    private DailyNanoRange range(LocalTime startAt, Duration duration) {
        return SimpleDailyNanoRange.of(startAt, duration);
    }
}
