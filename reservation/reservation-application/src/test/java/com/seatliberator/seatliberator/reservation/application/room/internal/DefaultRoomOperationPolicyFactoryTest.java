package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Application: RoomOperationPolicy Factory")
public class DefaultRoomOperationPolicyFactoryTest {

    RoomOperationPolicyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultRoomOperationPolicyFactory();
    }

    @Test
    @DisplayName("command 값으로 RoomOperationPolicy를 생성한다")
    void create_operation_policy_from_command() {
        var command = new RoomOperationPolicyFactoryCommand(
                4,
                Duration.ofHours(2),
                RoomOperationStatus.CLOSE,
                LocalTime.of(8, 0),
                LocalTime.of(10, 0)
        );

        var operationPolicy = factory.create(command);

        assertThat(operationPolicy.getMaxReservationPerUser()).isEqualTo(command.maxReservationPerUser());
        assertThat(operationPolicy.getMaxReservationDuration()).isEqualTo(command.maxReservationDuration());
        assertThat(operationPolicy.getOperationStatus()).isEqualTo(command.operationStatus());
        assertThat(operationPolicy.getOperationHours().startAt()).isEqualTo(command.openAt());
        assertThat(operationPolicy.getOperationHours().endAt()).isEqualTo(command.closeAt());
    }
}
