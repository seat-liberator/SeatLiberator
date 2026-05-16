package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindAvailableSlotsBySeatUseCase;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyReader;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancyFilter;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.application.booking.service.BookingTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindAvailableSlotsBySeatUseCase 테스트")
public class FindAvailableSlotsBySeatUseCaseTest {
    @Mock
    SeatReader seatReader;

    @Mock
    SeatTimeSlotReader slotReader;

    @Mock
    SeatOccupancyReader occupancyReader;

    FindAvailableSlotsBySeatUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new FindAvailableSlotsBySeatService(seatReader, slotReader, occupancyReader);
    }

    @Test
    @DisplayName("좌석별 사용 가능 슬롯 조회 시 seatId와 날짜 범위로 점유 criteria를 만들어 조회한다")
    void find_available_slots_builds_expected_occupancy_filter() {
        var query = findAvailableSlotsBySeatQuery();
        var slots = slots();

        when(slotReader.findBySeatId(query.seatId())).thenReturn(slots);
        when(occupancyReader.findByCriteria(any(SeatOccupancyFilter.class))).thenReturn(List.of());

        useCase.findAtDateRange(query);

        var captor = ArgumentCaptor.forClass(SeatOccupancyFilter.class);
        verify(slotReader).findBySeatId(query.seatId());
        verify(occupancyReader).findByCriteria(captor.capture());
        verifyNoInteractions(seatReader);
        verifyNoMoreInteractions(slotReader, occupancyReader);

        var actual = captor.getValue();
        assertThat(actual.getSlotIds()).containsExactlyInAnyOrder(MORNING_SLOT_ID, AFTERNOON_SLOT_ID);
        assertThat(actual.getRange()).isEqualTo(DATE_RANGE);
    }

    @Test
    @DisplayName("날짜별로 이미 점유된 슬롯을 제외하고 사용 가능 슬롯을 반환한다")
    void find_available_slots_excludes_occupied_slots_by_date() {
        var query = findAvailableSlotsBySeatQuery();
        var morningSlot = morningSlot();
        var afternoonSlot = afternoonSlot();
        var slots = List.of(morningSlot, afternoonSlot);

        when(slotReader.findBySeatId(query.seatId())).thenReturn(slots);
        when(occupancyReader.findByCriteria(any(SeatOccupancyFilter.class))).thenReturn(List.of(
                occupancy(morningSlot, RANGE_START_DATE),
                occupancy(afternoonSlot, RANGE_START_DATE.plusDays(1))
        ));

        var result = useCase.findAtDateRange(query);

        assertThat(result).containsOnlyKeys(RANGE_START_DATE, RANGE_START_DATE.plusDays(1), RANGE_END_DATE);
        assertThat(result.get(RANGE_START_DATE))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(SeatTimeSlotResult.from(afternoonSlot));
        assertThat(result.get(RANGE_START_DATE.plusDays(1)))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(SeatTimeSlotResult.from(morningSlot));
        assertThat(result.get(RANGE_END_DATE))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                        SeatTimeSlotResult.from(morningSlot),
                        SeatTimeSlotResult.from(afternoonSlot)
                );
    }

    @Test
    @DisplayName("점유 결과가 없으면 날짜별로 모든 슬롯을 반환한다")
    void find_available_slots_returns_all_slots_when_no_occupancy() {
        var query = findAvailableSlotsBySeatQuery();
        var slots = slots();

        when(slotReader.findBySeatId(query.seatId())).thenReturn(slots);
        when(occupancyReader.findByCriteria(any(SeatOccupancyFilter.class))).thenReturn(List.of());

        var result = useCase.findAtDateRange(query);

        assertThat(result.values()).hasSize(3);
        assertThat(result.values()).allSatisfy(availableSlots ->
                assertThat(availableSlots)
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactlyElementsOf(slots.stream().map(SeatTimeSlotResult::from).toList())
        );
    }
}
