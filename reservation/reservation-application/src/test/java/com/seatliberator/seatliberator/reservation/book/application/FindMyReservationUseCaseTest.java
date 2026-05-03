package com.seatliberator.seatliberator.reservation.book.application;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindMyReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindMyReservationQuery;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.ReservationRangeOverlapCriteria;
import com.seatliberator.seatliberator.reservation.application.booking.service.ReservationQueryService;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleTimeRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture.createReservation;
import static com.seatliberator.seatliberator.reservation.domain.shared.TimeRangeFixture.createRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find My Reservation Use Case")
public class FindMyReservationUseCaseTest {
    @Mock
    ReservationReader reader;

    FindMyReservationUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new ReservationQueryService(reader);
    }

    @Test
    @DisplayName("내 예약 조회 시 userId, status, range를 criteria에 담아서 reader에 전달한다")
    void find_with_query_builds_expected_criteria() {
        var range = createRange();

        var query = new FindMyReservationQuery("user-1", range, ReservationStatus.RESERVED);

        when(reader.findAllOverlapping(any(ReservationRangeOverlapCriteria.class)))
                .thenReturn(List.of());

        useCase.find(query);

        var captor = ArgumentCaptor.forClass(ReservationRangeOverlapCriteria.class);

        verify(reader, only()).findAllOverlapping(captor.capture());

        var actual = captor.getValue();

        assertThat(actual.range()).isEqualTo(SimpleTimeRange.from(range));
        assertThat(actual.filter().userIds()).containsExactlyInAnyOrder("user-1");
        assertThat(actual.filter().statuses()).containsExactly(ReservationStatus.RESERVED);
        assertThat(actual.filter().excludedIds()).isEmpty();
    }

    @Test
    @DisplayName("reader가 반환한 예약들을 ReservationResult로 변환해 반환한다")
    void find_maps_reader_result_to_reservation_result() {
        var range = createRange();

        var query = new FindMyReservationQuery("user-1", range, ReservationStatus.RESERVED);

        var reservation = createReservation();

        when(reader.findAllOverlapping(any(ReservationRangeOverlapCriteria.class)))
                .thenReturn(List.of(reservation));

        var result = useCase.find(query);

        assertThat(result).hasSize(1);
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                        ReservationResult.of(reservation)
                );
    }

    @Test
    @DisplayName("조회 결과가 없으면 빈 리스트 반환한다")
    void return_empty_list_when_reader_result_empty() {
        var range = createRange();

        var query = new FindMyReservationQuery("user-1", range, ReservationStatus.RESERVED);

        when(reader.findAllOverlapping(any(ReservationRangeOverlapCriteria.class)))
                .thenReturn(List.of());

        var result = useCase.find(query);

        assertThat(result).isEmpty();
    }
}
