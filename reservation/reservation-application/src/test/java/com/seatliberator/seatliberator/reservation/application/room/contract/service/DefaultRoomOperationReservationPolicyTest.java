package com.seatliberator.seatliberator.reservation.application.room.contract.service;

import com.seatliberator.seatliberator.reservation.application.room.contract.RoomOperationReservationPolicy;
import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyReason;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Room Operation Reservation Policy")
public class DefaultRoomOperationReservationPolicyTest {

    @Mock
    RoomReader roomReader;

    RoomOperationReservationPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new DefaultRoomOperationReservationPolicy(roomReader, fixedClock);
    }

    @Test
    @DisplayName("방을 찾을 수 없으면 거절한다")
    void reject_when_room_not_found() {
        var locator = SimpleSeatLocator.of("missing-room", "seat-1");
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.empty());

        var result = policy.evaluate(locator, InstantRangeFixture.get("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z"));

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.ROOM_NOT_FOUND);
    }

    @Test
    @DisplayName("방 운영 상태가 CLOSE이면 거절한다")
    void reject_when_room_operation_is_closed() {
        var locator = SimpleSeatLocator.of("study-room-1", "seat-1");
        var room = new RoomFixture.Builder()
                .roomId(locator.roomId())
                .operationPolicy(new RoomOperationPolicyFixture.Builder()
                        .operationStatus(RoomOperationStatus.CLOSE)
                        .build())
                .build();
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.of(room));

        var result = policy.evaluate(locator, InstantRangeFixture.get("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z"));

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.ROOM_OPERATION_CLOSED);
    }

    @Test
    @DisplayName("최대 예약 가능 시간을 초과하면 거절한다")
    void reject_when_reservation_duration_exceeds_limit() {
        var locator = SimpleSeatLocator.of("study-room-1", "seat-1");
        var room = new RoomFixture.Builder()
                .roomId(locator.roomId())
                .operationPolicy(new RoomOperationPolicyFixture.Builder()
                        .maxReservationDuration(Duration.ofMinutes(30))
                        .operationStatus(RoomOperationStatus.OPEN)
                        .build())
                .build();
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.of(room));

        var result = policy.evaluate(locator, InstantRangeFixture.get("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z"));

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.MAX_RESERVATION_DURATION_EXCEEDED);
    }

    @Test
    @DisplayName("예약 시간이 운영 시간 구간 안에 있으면 허용한다")
    void accept_when_reservation_range_is_within_operation_schedule() {
        var locator = SimpleSeatLocator.of("study-room-1", "seat-1");
        var room = new RoomFixture.Builder()
                .roomId(locator.roomId())
                .operationPolicy(new RoomOperationPolicyFixture.Builder()
                        .maxReservationDuration(Duration.ofHours(2))
                        .operationStatus(RoomOperationStatus.OPEN)
                        .operationSchedule(operationTimeRange())
                        .build())
                .build();
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.of(room));

        var result = policy.evaluate(locator, InstantRangeFixture.get("2026-01-01T09:00:00Z", "2026-01-01T10:00:00Z"));

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.ROOM_OPERATION_AVAILABLE);
    }

    @Test
    @DisplayName("예약 시작 시간이 운영 시간 구간 밖이면 거절한다")
    void reject_when_reservation_start_is_out_of_operation_schedule() {
        var locator = SimpleSeatLocator.of("study-room-1", "seat-1");
        var room = new RoomFixture.Builder()
                .roomId(locator.roomId())
                .operationPolicy(new RoomOperationPolicyFixture.Builder()
                        .maxReservationDuration(Duration.ofHours(2))
                        .operationStatus(RoomOperationStatus.OPEN)
                        .operationSchedule(operationTimeRange())
                        .build())
                .build();
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.of(room));

        var result = policy.evaluate(locator, InstantRangeFixture.get("2026-01-01T07:00:00Z", "2026-01-01T08:00:00Z"));

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
    }

    @Test
    @DisplayName("예약 시간이 운영 시간 구간 사이의 공백을 걸치면 거절한다")
    void reject_when_reservation_range_spans_gap_between_operation_schedule() {
        var locator = SimpleSeatLocator.of("study-room-1", "seat-1");
        var room = new RoomFixture.Builder()
                .roomId(locator.roomId())
                .operationPolicy(new RoomOperationPolicyFixture.Builder()
                        .maxReservationDuration(Duration.ofHours(2))
                        .operationStatus(RoomOperationStatus.OPEN)
                        .operationSchedule(operationTimeRange())
                        .build())
                .build();
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.of(room));

        var result = policy.evaluate(locator, InstantRangeFixture.get("2026-01-01T11:30:00Z", "2026-01-01T12:30:00Z"));

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
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
