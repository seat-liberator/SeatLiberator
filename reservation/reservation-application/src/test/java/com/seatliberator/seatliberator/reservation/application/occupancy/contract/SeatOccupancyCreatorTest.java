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

import java.util.Collection;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.application.occupancy.OccupancyTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SeatOccupancyCreator 테스트")
public class SeatOccupancyCreatorTest {
    @Mock
    SeatOccupancyStore store;

    @Mock
    SeatTimeSlotBundlePolicy slotBundlePolicy;

    SeatOccupancyCreator creator;

    @BeforeEach
    void run() {
        creator = new SeatOccupancyCreator(store, slotBundlePolicy, CLOCK);
    }

    @Test
    @DisplayName("점유 생성 시 슬롯 묶음 정책을 검증하고 점유들을 저장한다")
    void create_validates_slot_bundle_and_saves_occupancies() {
        when(store.saveAll(anyCollection())).thenAnswer(invocation ->
                List.copyOf(invocation.<Collection<SeatOccupancy>>getArgument(0))
        );

        var result = creator.create(RESERVATION_ID, SLOT_IDS, OCCUPANCY_DATE);

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
        assertThat(result).containsExactlyElementsOf(saved);
    }

    @Test
    @DisplayName("store가 반환한 점유 목록을 반환한다")
    void create_returns_saved_occupancies() {
        var savedOccupancies = occupancies();

        when(store.saveAll(anyCollection())).thenReturn(savedOccupancies);

        var result = creator.create(RESERVATION_ID, SLOT_IDS, OCCUPANCY_DATE);

        assertThat(result).isSameAs(savedOccupancies);
    }

    @Test
    @DisplayName("슬롯 묶음 정책이 거절하면 점유를 저장하지 않는다")
    void do_not_save_occupancies_when_slot_bundle_policy_rejected() {
        doThrow(new ReservationApplicationPolicyException(SeatTimeSlotPolicyReason.EMPTY_SLOT))
                .when(slotBundlePolicy)
                .validate(SLOT_IDS);

        assertThatThrownBy(() -> creator.create(RESERVATION_ID, SLOT_IDS, OCCUPANCY_DATE))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(SeatTimeSlotPolicyReason.EMPTY_SLOT);

        verify(slotBundlePolicy, only()).validate(SLOT_IDS);
        verifyNoInteractions(store);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<Collection<SeatOccupancy>> occupancyCaptor() {
        return ArgumentCaptor.forClass(Collection.class);
    }
}
