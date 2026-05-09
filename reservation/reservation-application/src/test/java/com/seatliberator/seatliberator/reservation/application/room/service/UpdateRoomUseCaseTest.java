package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.internal.RoomOperationPolicyProvisioner;
import com.seatliberator.seatliberator.reservation.application.room.port.in.UpdateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.time.Clock;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Update Room UseCase")
public class UpdateRoomUseCaseTest extends AbstractRoomServiceTest<UpdateRoomUseCase> {

    @Mock
    RoomOperationPolicyProvisioner policyProvisioner;

    @Override
    UpdateRoomUseCase init(RoomReader reader, RoomStore store, Clock clock) {
        return new RoomCommandService(reader, store, policyProvisioner, clock);
    }

    @Test
    @DisplayName("방 정보 변경 시 oldRoomId에 해당하는 방의 roomId를 newRoomId로 바꾼다")
    void update_with_newRoomId() {
        var oldRoomId = "old-room-1";
        var newRoomId = "new-room-1";
        var command = new UpdateRoomCommand(oldRoomId, newRoomId);

        var oldRoom = new RoomFixture.Builder().roomId(oldRoomId).createdAt(now).build();
        when(reader.findByRoomId(oldRoomId)).thenReturn(Optional.of(oldRoom));

        var result = useCase.update(command);

        assertThat(result.roomId()).isEqualTo(newRoomId);

        var roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(reader).findByRoomId(oldRoomId);
        verify(store).save(roomCaptor.capture());

        var saved = roomCaptor.getValue();
        assertThat(saved.getRoomId()).isEqualTo(newRoomId);
    }

    @Test
    @DisplayName("방 정보 변경 시 기존 방 Id와 새 방 Id가 동일하면 변경하지 않고 바로 결과 반환")
    void fast_return_when_oldRoomId_and_newRoomId_is_same() {
        var oldRoomId = "old-room-1";
        var command = new UpdateRoomCommand(oldRoomId, oldRoomId);

        var oldRoom = new RoomFixture.Builder().roomId(oldRoomId).createdAt(now).build();
        when(reader.findByRoomId(oldRoomId)).thenReturn(Optional.of(oldRoom));

        var result = useCase.update(command);

        assertThat(result.roomId()).isEqualTo(oldRoomId);

        verify(reader).findByRoomId(oldRoomId);
        verify(store, never()).save(any());
    }

    @Test
    @DisplayName("방 정보 변경 시 Command의 oldRoomId에 해당하는 방이 없으면 ROOM_NOT_FOUND 예외")
    void throw_exception_when_room_not_found() {
        var oldRoomId = "old-room-1";
        var newRoomId = "new-room-1";
        var command = new UpdateRoomCommand(oldRoomId, newRoomId);

        when(reader.findByRoomId(oldRoomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.update(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

        verify(reader).findByRoomId(oldRoomId);
        verify(store, never()).save(any());
    }
}

