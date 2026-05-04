package com.seatliberator.seatliberator.reservation.room.application;

import com.seatliberator.seatliberator.reservation.application.room.port.in.FindRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.ListRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.query.FindRoomQuery;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.service.RoomQueryService;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Room Query UseCase")
public class RoomQueryServiceTest {
    @Mock
    RoomReader reader;

    @Nested
    @DisplayName("List Room UseCase")
    class ListRoomUseCaseTest {
        ListRoomUseCase useCase;

        @BeforeEach
        void run() {
            useCase = new RoomQueryService(reader);
        }

        @Test
        @DisplayName("전체 방 목록을 조회한다")
        void list_rooms() {
            var room = RoomFixture.get();
            when(reader.findAll()).thenReturn(List.of(room));

            var result = useCase.list();

            assertThat(result)
                    .hasSize(1)
                    .first()
                    .extracting("roomId")
                    .isEqualTo(room.getRoomId());

            verify(reader).findAll();
        }
    }

    @Nested
    @DisplayName("Find Room UseCase")
    class FindRoomUseCaseTest {
        FindRoomUseCase useCase;

        @BeforeEach
        void run() {
            useCase = new RoomQueryService(reader);
        }

        @Test
        @DisplayName("roomId에 해당하는 방을 조회한다")
        void find_room() {
            var room = RoomFixture.get();
            var query = new FindRoomQuery(room.getRoomId());
            when(reader.findByRoomId(room.getRoomId())).thenReturn(Optional.of(room));

            var result = useCase.find(query);

            assertThat(result.roomId()).isEqualTo(room.getRoomId());
            verify(reader).findByRoomId(room.getRoomId());
        }

        @Test
        @DisplayName("roomId에 해당하는 방이 없으면 ROOM_NOT_FOUND 예외")
        void throw_exception_when_room_not_found() {
            var query = new FindRoomQuery("missing-room");
            when(reader.findByRoomId(query.roomId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.find(query))
                    .isInstanceOf(ReservationApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReservationApplicationErrorCode.ROOM_NOT_FOUND);

            verify(reader).findByRoomId(query.roomId());
        }
    }
}
