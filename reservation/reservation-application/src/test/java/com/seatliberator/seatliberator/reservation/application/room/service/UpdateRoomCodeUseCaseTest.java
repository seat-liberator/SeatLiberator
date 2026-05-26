package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.room.internal.RoomOperationPolicyProvisioner;
import com.seatliberator.seatliberator.reservation.application.room.port.in.UpdateRoomCodeUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.room.RoomTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Room Code UseCase")
public class UpdateRoomCodeUseCaseTest {
    @Mock
    RoomReader reader;

    @Mock
    RoomStore store;

    @Mock
    RoomOperationPolicyProvisioner policyProvisioner;

    Clock clock;

    UpdateRoomCodeUseCase useCase;

    @BeforeEach
    void run() {
        clock = TestClock.getFixed();
        useCase = new RoomCommandService(reader, store, policyProvisioner, clock);
    }

    @Test
    @DisplayName("roomId에 해당하는 방의 code를 변경한다")
    void update_room_code() {
        var command = updateRoomCodeCommand();
        var roomId = command.roomId();
        var room = room();

        when(reader.findById(roomId)).thenReturn(Optional.of(room));
        when(reader.existsByCode(command.newCode())).thenReturn(false);

        var result = useCase.update(command);

        verify(reader).findById(roomId);
        verify(reader).existsByCode(command.newCode());
        verify(store).save(room);

        assertThat(room.getCode()).isEqualTo(command.newCode());
        assertThat(result.code()).isEqualTo(command.newCode());
    }

    @Test
    @DisplayName("기존 code와 새 code가 같으면 변경하지 않고 결과를 반환한다")
    void return_when_code_is_same() {
        var command = updateRoomCodeCommand();
        var roomId = command.roomId();
        var room = room(roomId, command.newCode());

        when(reader.findById(roomId)).thenReturn(Optional.of(room));

        var result = useCase.update(command);

        verify(reader).findById(roomId);
        verify(reader, never()).existsByCode(any());
        verify(store, never()).save(any());

        assertThat(result.code()).isEqualTo(command.newCode());
    }

    @Test
    @DisplayName("roomId에 해당하는 방이 없으면 ROOM_NOT_FOUND 예외")
    void throw_exception_when_room_not_found() {
        var command = updateRoomCodeCommand();
        var roomId = command.roomId();

        when(reader.findById(roomId)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.update(command))
                .hasErrorCode(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

        verify(reader).findById(roomId);
        verify(reader, never()).existsByCode(any());
        verify(store, never()).save(any());
    }

    @Test
    @DisplayName("변경할 code의 방이 이미 있으면 ROOM_ALREADY_EXISTS 예외")
    void throw_exception_when_room_already_exists() {
        var command = updateRoomCodeCommand();
        var roomId = command.roomId();
        var room = room();

        when(reader.findById(roomId)).thenReturn(Optional.of(room));
        when(reader.existsByCode(command.newCode())).thenReturn(true);

        assertThatApplicationThrownBy(() -> useCase.update(command))
                .hasErrorCode(ReservationApplicationErrorCode.ROOM_ALREADY_EXISTS);

        verify(reader).findById(roomId);
        verify(reader).existsByCode(command.newCode());
        verify(store, never()).save(any());
    }
}
