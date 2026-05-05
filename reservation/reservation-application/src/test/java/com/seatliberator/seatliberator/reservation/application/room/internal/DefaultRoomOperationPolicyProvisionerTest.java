package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Application: RoomOperationPolicy Provisioner")
public class DefaultRoomOperationPolicyProvisionerTest {

    RoomOperationPolicyProvisioner provisioner;

    @BeforeEach
    void setUp() {
        provisioner = new DefaultRoomOperationPolicyProvisioner();
    }

    @Test
    @DisplayName("기본 RoomOperationPolicy를 제공한다")
    void provide_default_operation_policy() {
        var operationPolicy = provisioner.provide();

        assertThat(operationPolicy.getMaxReservationPerUser()).isEqualTo(5);
        assertThat(operationPolicy.getMaxReservationDuration()).isEqualTo(Duration.ofHours(4));
        assertThat(operationPolicy.getOperationStatus()).isEqualTo(RoomOperationStatus.OPEN);
        assertThat(operationPolicy.getOperationHours().getOpenAt()).isEqualTo(LocalTime.of(6, 0));
        assertThat(operationPolicy.getOperationHours().getCloseAt()).isEqualTo(LocalTime.of(0, 0));
    }
}
