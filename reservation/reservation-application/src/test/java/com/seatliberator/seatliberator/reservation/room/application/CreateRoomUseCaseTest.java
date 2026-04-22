package com.seatliberator.seatliberator.reservation.room.application;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;
import com.seatliberator.seatliberator.reservation.room.application.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.command.CreateRoomCommand;
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

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Room UseCase")
public class CreateRoomUseCaseTest {
    @Mock
    RoomStore store;

    @Mock
    RoomReader reader;

    CreateRoomUseCase useCase;

    Clock clock = fixedClock;

    @BeforeEach
    void run() {
        useCase = new RoomCommandService(reader, store, clock);
    }

    @Test
    @DisplayName("방 생성 시 Command의 roomId로 방을 만든다")
    void create_with_roomId() {
        var roomId = "study-room-1";
        var command = new CreateRoomCommand(roomId);

        when(reader.isExistsByRoomId(roomId)).thenReturn(false);

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

        when(reader.isExistsByRoomId(roomId)).thenReturn(true);
        assertThatThrownBy(() -> useCase.create(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.ROOM_ALREADY_EXISTS);
    }
}