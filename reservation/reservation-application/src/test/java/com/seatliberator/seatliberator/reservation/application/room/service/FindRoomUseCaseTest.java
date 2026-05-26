package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.port.in.FindRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.application.room.RoomTestSupport.findRoomQuery;
import static com.seatliberator.seatliberator.reservation.application.room.RoomTestSupport.room;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find Room UseCase")
public class FindRoomUseCaseTest {
    @Mock
    RoomReader reader;

    FindRoomUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new RoomQueryService(reader);
    }

    @Test
    @DisplayName("roomId에 해당하는 방을 조회한다")
    void find_room() {
        var query = findRoomQuery();
        var roomId = query.roomId();
        var room = room();

        when(reader.findById(roomId)).thenReturn(Optional.of(room));

        var result = useCase.find(query);

        assertThat(result.roomId()).isEqualTo(room.getId());
        assertThat(result.code()).isEqualTo(room.getCode());
        verify(reader).findById(roomId);
    }

    @Test
    @DisplayName("roomId에 해당하는 방이 없으면 ROOM_NOT_FOUND 예외")
    void throw_exception_when_room_not_found() {
        var query = findRoomQuery();
        var roomId = query.roomId();

        when(reader.findById(roomId)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.find(query))
                .hasErrorCode(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

        verify(reader).findById(roomId);
    }
}
