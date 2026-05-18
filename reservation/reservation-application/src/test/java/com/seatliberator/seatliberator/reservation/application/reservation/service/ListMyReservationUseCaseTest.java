package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.ListMyReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.ListMyReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.reservation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListMyReservationUseCase 테스트")
public class ListMyReservationUseCaseTest {
    @Mock
    ReservationReader reader;

    ListMyReservationUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new ListMyReservationService(reader);
    }

    @Test
    @DisplayName("내 예약 목록 조회 시 userId로 reader에 조회를 위임한다")
    void list_delegates_to_reader_with_user_id() {
        var query = ListMyReservationQuery.of("user-1");

        when(reader.findByUserId(query.userId()))
                .thenReturn(List.of());

        useCase.list(query);

        verify(reader, only()).findByUserId(query.userId());
    }

    @Test
    @DisplayName("reader가 반환한 예약들을 ReservationResult로 변환해 반환한다")
    void list_maps_reader_result_to_reservation_result() {
        var query = ListMyReservationQuery.of("user-1");

        when(reader.findByUserId(query.userId()))
                .thenReturn(List.of(reservation()));

        var result = useCase.list(query);

        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(ReservationResult.from(reservation()));
    }

    @Test
    @DisplayName("조회 결과가 없으면 빈 리스트를 반환한다")
    void return_empty_list_when_reader_result_empty() {
        var query = ListMyReservationQuery.of("user-1");

        when(reader.findByUserId(query.userId()))
                .thenReturn(List.of());

        var result = useCase.list(query);

        assertThat(result).isEmpty();
    }
}
