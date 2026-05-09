package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.internal.RoomOperationPolicyProvisioner;
import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomOperationPolicyFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Create Room UseCase")
public class CreateRoomUseCaseTest extends AbstractRoomServiceTest<CreateRoomUseCase> {

    @Mock
    RoomOperationPolicyProvisioner policyProvisioner;

    @Override
    CreateRoomUseCase init(RoomReader reader, RoomStore store, Clock clock) {
        return new RoomCommandService(reader, store, policyProvisioner, clock);
    }

    @Test
    @DisplayName("방 생성 시 Command의 roomId로 방을 만든다")
    void create_with_roomId() {
        var roomId = "study-room-1";
        var command = new CreateRoomCommand(roomId);

        when(reader.existsByRoomId(roomId)).thenReturn(false);
        when(policyProvisioner.provide()).thenReturn(RoomOperationPolicyFixture.get());

        useCase.create(command);

        var roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(store).save(roomCaptor.capture());

        var saved = roomCaptor.getValue();
        assertThat(saved.getRoomId()).isEqualTo(roomId);
    }

    @Test
    @DisplayName("방 생성 시 동일한 roomId가 이미 있으면 ROOM_ALREADY_EXISTS 예외")
    void throw_exception_when_create_with_exists_roomId() {
        var roomId = "study-room-1";
        var command = new CreateRoomCommand(roomId);

        when(reader.existsByRoomId(roomId)).thenReturn(true);
        assertThatThrownBy(() -> useCase.create(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.ROOM_ALREADY_EXISTS);
    }
}
