package com.seatliberator.seatliberator.reservation.application.occupancy.contract;

import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyStore;
import com.seatliberator.seatliberator.reservation.application.seat.contract.SeatTimeSlotBundlePolicy;
import com.seatliberator.seatliberator.reservation.application.seat.contract.SeatTimeSlotPolicyReason;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.application.occupancy.OccupancyTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SeatOccupancyAllocator 테스트")
public class SeatOccupancyAllocatorTest {
    @Mock
    SeatOccupancyStore store;

    @Mock
    SeatTimeSlotBundlePolicy slotBundlePolicy;

    SeatOccupancyAllocator allocator;

    @BeforeEach
    void run() {
        allocator = new SeatOccupancyAllocator(store, slotBundlePolicy, CLOCK);
    }

    @Test
    @DisplayName("점유 생성 시 슬롯 묶음 정책을 검증하고 점유들을 저장한다")
    void allocate_validates_slot_bundle_and_saves_occupancies() {
        var result = allocator.allocate(RESERVATION_ID, SLOT_IDS, OCCUPANCY_DATE);

        var captor = occupancyCaptor();

        verify(slotBundlePolicy).validate(SLOT_IDS);
        verify(store).saveAll(captor.capture());
        verifyNoMoreInteractions(slotBundlePolicy, store);

        var saved = List.copyOf(captor.getValue());

        assertThat(saved).hasSize(2);
        assertThat(saved)
                .extracting(SeatOccupancy::getSeatTimeSlotId)
                .containsExactlyElementsOf(SLOT_IDS);
        assertThat(saved).allSatisfy(occupancy -> {
            assertThat(occupancy.getReservationId()).isEqualTo(RESERVATION_ID);
            assertThat(occupancy.getOccupancyDate()).isEqualTo(OCCUPANCY_DATE);
            assertThat(occupancy.getCreatedAt()).isEqualTo(NOW);
        });

        assertThat(result.reservationId()).isEqualTo(RESERVATION_ID);
        assertThat(result.slotIds()).containsExactlyInAnyOrderElementsOf(SLOT_IDS);
        assertThat(result.occupancyDate()).isEqualTo(OCCUPANCY_DATE);
    }

    @Test
    @DisplayName("슬롯 묶음 정책이 거절하면 점유를 저장하지 않는다")
    void do_not_save_occupancies_when_slot_bundle_policy_rejected() {
        doThrow(new ReservationApplicationPolicyException(SeatTimeSlotPolicyReason.EMPTY_SLOT))
                .when(slotBundlePolicy)
                .validate(SLOT_IDS);

        assertThatThrownBy(() -> allocator.allocate(RESERVATION_ID, SLOT_IDS, OCCUPANCY_DATE))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(SeatTimeSlotPolicyReason.EMPTY_SLOT);

        verify(slotBundlePolicy, only()).validate(SLOT_IDS);
        verifyNoInteractions(store);
    }

    @Test
    @DisplayName("slotIds가 비어있으면 슬롯 묶음 정책을 검증하지 않고 예외")
    void throw_exception_when_slot_ids_empty() {
        assertThatThrownBy(() -> allocator.allocate(RESERVATION_ID, List.of(), OCCUPANCY_DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("slotIds must not be empty.");

        verifyNoInteractions(slotBundlePolicy, store);
    }

    @Test
    @DisplayName("slotIds에 null 원소가 있으면 슬롯 묶음 정책을 검증하지 않고 예외")
    void throw_exception_when_slot_ids_contains_null() {
        assertThatThrownBy(() -> allocator.allocate(RESERVATION_ID, Arrays.asList(MORNING_SLOT_ID, null), OCCUPANCY_DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("slotIds must not contain null.");

        verifyNoInteractions(slotBundlePolicy, store);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<Collection<SeatOccupancy>> occupancyCaptor() {
        return ArgumentCaptor.forClass(Collection.class);
    }
}
