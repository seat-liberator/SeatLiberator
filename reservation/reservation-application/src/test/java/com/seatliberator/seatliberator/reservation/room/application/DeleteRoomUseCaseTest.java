package com.seatliberator.seatliberator.reservation.room.application;

import com.seatliberator.seatliberator.reservation.room.application.port.in.DeleteRoomUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.DeleteRoomCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.room.application.service.RoomCommandService;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Room UseCase")
public class DeleteRoomUseCaseTest {
    @Mock
    RoomStore store;

    @Mock
    RoomReader reader;

    DeleteRoomUseCase useCase;

    Clock clock = fixedClock;

    @BeforeEach
    void run() {
        useCase = new RoomCommandService(reader, store, clock);
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