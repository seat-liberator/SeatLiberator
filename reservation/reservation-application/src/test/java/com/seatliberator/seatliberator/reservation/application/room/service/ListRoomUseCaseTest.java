package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.port.in.ListRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.application.room.RoomTestSupport.room;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("List Room UseCase")
public class ListRoomUseCaseTest {
    @Mock
    RoomReader reader;

    ListRoomUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new RoomQueryService(reader);
    }

    @Test
    @DisplayName("전체 방 목록을 조회한다")
    void list_rooms() {
        var room = room();

        when(reader.findAll()).thenReturn(List.of(room));

        var result = useCase.list();

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting("roomId")
                .isEqualTo(room.getId());
        verify(reader).findAll();
    }
}
