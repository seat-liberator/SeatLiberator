package com.seatliberator.seatliberator.reservation.room.application;

import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.DeleteRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.UpdateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.DeleteRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.application.room.service.RoomCommandService;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Room Command UseCase")
public class RoomCommandUseCaseTest {
    @Mock
    RoomReader reader;

    @Mock
    RoomStore store;

    Clock clock = fixedClock;

    Instant now = clock.instant();

    @Nested
    @DisplayName("Create Room UseCase")
    class CreateRoomUseCaseTest {
        CreateRoomUseCase useCase;

        @BeforeEach
        void run() {
            useCase = new RoomCommandService(reader, store, clock);
        }

        @Test
        @DisplayName("방 생성 시 Command의 roomId로 방을 만든다")
        void create_with_roomId() {
            var roomId = "study-room-1";
            var command = new CreateRoomCommand(roomId);

            when(reader.existsByRoomId(roomId)).thenReturn(false);

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

    @Nested
    @DisplayName("Update Room UseCase")
    class UpdateRoomUseCaseTest {
        UpdateRoomUseCase useCase;

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

            var oldRoom = Room.of(oldRoomId, now);
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

    @Nested
    @DisplayName("Delete Room UseCase")
    class DeleteRoomUseCaseTest {
        DeleteRoomUseCase useCase;

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
}
