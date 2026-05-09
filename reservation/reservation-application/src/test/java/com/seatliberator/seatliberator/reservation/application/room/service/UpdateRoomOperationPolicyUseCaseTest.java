package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.RoomTestSupport;
import com.seatliberator.seatliberator.reservation.application.room.port.in.UpdateRoomOperationPolicyUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomOperationPolicyResult;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.util.Optional;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Update Room Operation Policy UseCase")
public class UpdateRoomOperationPolicyUseCaseTest extends AbstractRoomServiceTest<UpdateRoomOperationPolicyUseCase> {

    @Override
    UpdateRoomOperationPolicyUseCase init(RoomReader reader, RoomStore store, Clock clock) {
        return new RoomOperationPolicyCommandService(reader, store);
    }

    @Test
    @DisplayName("방 운영 정책 변경 시 해당하는 roomId 없으면 예외")
    void throw_exception_when_roomId_not_found() {
        var command = RoomTestSupport.updateRoomOperationPolicyCommand();
        var roomId = command.roomId();

        when(reader.findByRoomId(roomId)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.update(command))
                .hasErrorCode(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

        verify(reader).findByRoomId(roomId);
        verify(store, never()).save(any());
    }

    @Test
    @DisplayName("roomId에 해당하는 방의 운영 정책을 새로운 운영 정책으로 바꾼다.")
    void update_operation_policy() {
        var command = RoomTestSupport.updateRoomOperationPolicyCommand();
        var operationPolicy = RoomTestSupport.updatedOperationPolicy();
        var roomId = command.roomId();
        var room = RoomFixture.get();

        when(reader.findByRoomId(roomId)).thenReturn(Optional.of(room));

        var result = useCase.update(command);

        var roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(reader).findByRoomId(roomId);
        verify(store).save(roomCaptor.capture());

        var saved = roomCaptor.getValue();
        assertThat(saved).isSameAs(room);
        RoomTestSupport.assertEqualPolicy(saved.getOperationPolicy(), operationPolicy);
        assertThat(result).isEqualTo(RoomOperationPolicyResult.from(operationPolicy));
    }
}
