package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationCreator 테스트")
public class ReservationCreatorTest {
    @Mock
    ReservationCreateAuthorizer authorizer;

    @Mock
    ReservationStore store;

    ReservationCreator creator;

    @BeforeEach
    void run() {
        creator = new ReservationCreator(authorizer, store, CLOCK);
    }

    @Test
    @DisplayName("예약 생성 시 userId와 현재 시각으로 예약을 만들고 저장한다")
    void create_saves_reservation_with_user_id_and_reserved_at() {
        when(store.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = creator.create(USER_ID);

        var captor = ArgumentCaptor.forClass(Reservation.class);

        verify(store, only()).save(captor.capture());

        var saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getState().getStatus()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(saved.getState().getReservedAt()).isEqualTo(NOW);
        assertThat(result).isSameAs(saved);
    }

    @Test
    @DisplayName("store가 반환한 예약을 반환한다")
    void create_returns_saved_reservation() {
        var savedReservation = reservation();

        when(store.save(any(Reservation.class))).thenReturn(savedReservation);

        var result = creator.create(USER_ID);

        assertThat(result).isSameAs(savedReservation);
    }
}
