package com.seatliberator.seatliberator.reservation.application.room.contract.service;

import com.seatliberator.seatliberator.reservation.application.room.contract.RoomOperationReservationPolicy;
import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyReason;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.domain.room.OperationHourFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.shared.TimeRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
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
    @DisplayName("방 운영 정책상 예약 가능하면 승인한다")
    void accept_when_room_operation_policy_allows_reservation() {
        var locator = SimpleSeatLocator.of("study-room-1", "seat-1");
        var range = range("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z");
        var room = new RoomFixture.Builder()
                .roomId(locator.roomId())
                .operationPolicy(new RoomOperationPolicyFixture.Builder()
                        .maxReservationDuration(Duration.ofHours(2))
                        .operationStatus(RoomOperationStatus.OPEN)
                        .operationHours(new OperationHourFixture.Builder()
                                .openAt(LocalTime.of(6, 0))
                                .closeAt(LocalTime.of(22, 0))
                                .build())
                        .build())
                .build();
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.of(room));

        var result = policy.evaluate(locator, range);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.ROOM_OPERATION_AVAILABLE);
        verify(roomReader).findByRoomId(locator.roomId());
    }

    @Test
    @DisplayName("방을 찾을 수 없으면 거절한다")
    void reject_when_room_not_found() {
        var locator = SimpleSeatLocator.of("missing-room", "seat-1");
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.empty());

        var result = policy.evaluate(locator, range("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z"));

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

        var result = policy.evaluate(locator, range("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z"));

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

        var result = policy.evaluate(locator, range("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z"));

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.MAX_RESERVATION_DURATION_EXCEEDED);
    }

    @Test
    @DisplayName("운영 시간을 벗어나면 거절한다")
    void reject_when_range_is_out_of_operation_hours() {
        var locator = SimpleSeatLocator.of("study-room-1", "seat-1");
        var room = new RoomFixture.Builder()
                .roomId(locator.roomId())
                .operationPolicy(new RoomOperationPolicyFixture.Builder()
                        .maxReservationDuration(Duration.ofHours(2))
                        .operationStatus(RoomOperationStatus.OPEN)
                        .operationHours(new OperationHourFixture.Builder()
                                .openAt(LocalTime.of(9, 0))
                                .closeAt(LocalTime.of(18, 0))
                                .build())
                        .build())
                .build();
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.of(room));

        var result = policy.evaluate(locator, range("2026-01-01T08:00:00Z", "2026-01-01T09:00:00Z"));

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
    }

    @Test
    @DisplayName("자정을 넘기는 운영 시간 안에 있으면 승인한다")
    void accept_when_range_is_inside_overnight_operation_hours() {
        var locator = SimpleSeatLocator.of("study-room-1", "seat-1");
        var room = new RoomFixture.Builder()
                .roomId(locator.roomId())
                .operationPolicy(new RoomOperationPolicyFixture.Builder()
                        .maxReservationDuration(Duration.ofHours(2))
                        .operationStatus(RoomOperationStatus.OPEN)
                        .operationHours(new OperationHourFixture.Builder()
                                .openAt(LocalTime.of(22, 0))
                                .closeAt(LocalTime.of(2, 0))
                                .build())
                        .build())
                .build();
        when(roomReader.findByRoomId(locator.roomId())).thenReturn(Optional.of(room));

        var result = policy.evaluate(locator, range("2026-01-01T23:00:00Z", "2026-01-02T01:00:00Z"));

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.ROOM_OPERATION_AVAILABLE);
    }

    private TimeRange range(String startAt, String endAt) {
        return SimpleTimeRange.of(Instant.parse(startAt), Instant.parse(endAt));
    }
}
