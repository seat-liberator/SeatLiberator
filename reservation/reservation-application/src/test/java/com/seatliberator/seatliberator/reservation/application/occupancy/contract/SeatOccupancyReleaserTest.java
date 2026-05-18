package com.seatliberator.seatliberator.reservation.application.occupancy.contract;

import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyReader;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.application.occupancy.OccupancyTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SeatOccupancyReleaser 테스트")
public class SeatOccupancyReleaserTest {
    @Mock
    SeatOccupancyReader reader;

    @Mock
    SeatOccupancyStore store;

    SeatOccupancyReleaser releaser;

    @BeforeEach
    void run() {
        releaser = new SeatOccupancyReleaser(reader, store);
    }

    @Test
    @DisplayName("점유 해제 시 예약의 점유들을 삭제하고 해제 결과를 반환한다")
    void release_deletes_occupancies_and_returns_result() {
        var occupancies = occupancies();

        when(reader.findByReservationId(RESERVATION_ID)).thenReturn(occupancies);

        var result = releaser.release(RESERVATION_ID);

        verify(reader).findByReservationId(RESERVATION_ID);
        verify(store).deleteAll(occupancies);
        verifyNoMoreInteractions(reader, store);

        assertThat(result.reservationId()).isEqualTo(RESERVATION_ID);
        assertThat(result.slotIds()).containsExactlyInAnyOrderElementsOf(SLOT_IDS);
        assertThat(result.occupancyDate()).isEqualTo(OCCUPANCY_DATE);
    }

    @Test
    @DisplayName("예약의 점유가 없으면 점유를 삭제하지 않고 정책 거절 예외")
    void throw_exception_when_occupancies_empty() {
        when(reader.findByReservationId(RESERVATION_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> releaser.release(RESERVATION_ID))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(SeatOccupancyPolicyReason.EMPTY_OCCUPANCIES);

        verify(reader, only()).findByReservationId(RESERVATION_ID);
        verifyNoInteractions(store);
    }

    @Test
    @DisplayName("서로 다른 점유 날짜가 포함되어 있으면 점유를 삭제하지 않고 정책 거절 예외")
    void throw_exception_when_different_occupancy_date_included() {
        var occupancies = List.of(
                occupancy(MORNING_SLOT_ID, OCCUPANCY_DATE),
                occupancy(AFTERNOON_SLOT_ID, OCCUPANCY_DATE.plusDays(1))
        );

        when(reader.findByReservationId(RESERVATION_ID)).thenReturn(occupancies);

        assertThatThrownBy(() -> releaser.release(RESERVATION_ID))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(SeatOccupancyPolicyReason.DIFFERENT_OCCUPANCY_DATE_INCLUDED);

        verify(reader, only()).findByReservationId(RESERVATION_ID);
        verifyNoInteractions(store);
    }

    @Test
    @DisplayName("reservationId가 null이면 점유를 조회하지 않고 예외")
    void throw_exception_when_reservation_id_null() {
        assertThatThrownBy(() -> releaser.release(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("reservationId must not be null.");

        verifyNoInteractions(reader, store);
    }
}
