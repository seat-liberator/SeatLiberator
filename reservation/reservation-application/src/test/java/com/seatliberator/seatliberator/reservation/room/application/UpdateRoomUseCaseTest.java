package com.seatliberator.seatliberator.reservation.room.application;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;
import com.seatliberator.seatliberator.reservation.room.application.port.in.UpdateRoomUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.UpdateRoomCommand;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.room.application.service.RoomCommandService;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Room UseCase")
public class UpdateRoomUseCaseTest {
    @Mock
    RoomStore store;

    @Mock
    RoomReader reader;

    UpdateRoomUseCase useCase;

    Clock clock = fixedClock;

    Instant now = clock.instant();

    @BeforeEach
    void run() {
        useCase = new RoomCommandService(reader, store, clock);
    }

    @Test
    @DisplayName("방 정보 변경 시 oldRoomId에 해당하는 방의 roomId를 newRoomId로 바꾼다")
    void update_with_newRoomId() {
        var oldRoomId = "old-room-1";
        var newRoomId = "new-room-1";
        var command = new UpdateRoomCommand(oldRoomId, newRoomId);

        var oldRoom = Room.of(oldRoomId, now);
        when(reader.findByRoomId(oldRoomId)).thenReturn(Optional.of(oldRoom));

        var result = useCase.update(command);

        assertThat(result.id()).isEqualTo(newRoomId);

        var roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(reader).findByRoomId(oldRoomId);
        verify(store).save(roomCaptor.capture());

        var saved = roomCaptor.getValue();
        assertThat(saved.getRoomId()).isEqualTo(newRoomId);
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
