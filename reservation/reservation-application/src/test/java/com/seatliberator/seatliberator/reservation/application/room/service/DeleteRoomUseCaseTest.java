package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.internal.RoomOperationPolicyProvisioner;
import com.seatliberator.seatliberator.reservation.application.room.port.in.DeleteRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.DeleteRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Delete Room UseCase")
public class DeleteRoomUseCaseTest extends AbstractRoomServiceTest<DeleteRoomUseCase> {

    @Mock
    RoomOperationPolicyProvisioner policyProvisioner;

    @Override
    DeleteRoomUseCase init(RoomReader reader, RoomStore store, Clock clock) {
        return new RoomCommandService(reader, store, policyProvisioner, clock);
    }

    @Test
    @DisplayName("방 삭제 시 roomId에 해당하는 방이 없으면 예외")
    void throw_exception_when_room_not_found() {
        var roomId = "study-room-1";
        var command = new DeleteRoomCommand(roomId);

        when(reader.existsByRoomId(roomId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.delete(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

        verify(reader).existsByRoomId(roomId);
        verify(store, never()).deleteByRoomId(any());
    }

    @Test
    @DisplayName("방 삭제 시 roomId에 해당하는 방을 삭제한다")
    void delete_with_roomId() {
        var roomId = "study-room-1";
        var command = new DeleteRoomCommand(roomId);

        when(reader.existsByRoomId(roomId)).thenReturn(true);

        useCase.delete(command);

        verify(reader).existsByRoomId(roomId);
        verify(store).deleteByRoomId(roomId);
    }
}
