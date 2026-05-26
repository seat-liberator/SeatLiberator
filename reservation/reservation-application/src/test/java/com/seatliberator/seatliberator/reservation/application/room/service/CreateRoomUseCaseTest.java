package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.room.internal.RoomOperationPolicyProvisioner;
import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.room.RoomTestSupport.assertEqualPolicy;
import static com.seatliberator.seatliberator.reservation.application.room.RoomTestSupport.createRoomCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Room UseCase")
public class CreateRoomUseCaseTest {
    @Mock
    RoomReader reader;

    @Mock
    RoomStore store;

    @Mock
    RoomOperationPolicyProvisioner policyProvisioner;

    Clock clock;

    CreateRoomUseCase useCase;

    @BeforeEach
    void run() {
        clock = TestClock.getFixed();
        useCase = new RoomCommandService(reader, store, policyProvisioner, clock);
    }

    @Test
    @DisplayName("code에 해당하는 방을 만든다")
    void create_room() {
        var command = createRoomCommand();
        var operationPolicy = RoomOperationPolicyFixture.get();

        when(reader.existsByCode(command.code())).thenReturn(false);
        when(policyProvisioner.provide()).thenReturn(operationPolicy);

        var result = useCase.create(command);

        var roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(reader).existsByCode(command.code());
        verify(policyProvisioner).provide();
        verify(store).save(roomCaptor.capture());

        var saved = roomCaptor.getValue();
        assertThat(saved.getCode()).isEqualTo(command.code());
        assertThat(saved.getCreatedAt()).isEqualTo(clock.instant());
        assertEqualPolicy(saved.getOperationPolicy(), operationPolicy);
        assertThat(result.code()).isEqualTo(command.code());
        assertThat(result.createdAt()).isEqualTo(clock.instant());
    }

    @Test
    @DisplayName("같은 code의 방이 이미 있으면 ROOM_ALREADY_EXISTS 예외")
    void throw_exception_when_room_already_exists() {
        var command = createRoomCommand();

        when(reader.existsByCode(command.code())).thenReturn(true);

        assertThatApplicationThrownBy(() -> useCase.create(command))
                .hasErrorCode(ReservationApplicationErrorCode.ROOM_ALREADY_EXISTS);

        verify(reader).existsByCode(command.code());
        verify(policyProvisioner, never()).provide();
        verify(store, never()).save(any());
    }
}
