package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.UseReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.application.reservation.service.ReservationTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UseReservationUseCase 테스트")
public class UseReservationServiceTest {
    @Mock
    ReservationReader reader;

    @Mock
    ReservationStore store;

    UseReservationUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new UseReservationService(reader, store, new ReservationOwnershipPolicy(), CLOCK);
    }

    @Test
    @DisplayName("예약 사용 시 예약을 사용 상태로 변경하고 저장한다")
    void use_reservation() {
        var reservation = reservationWithId();
        var command = useReservationCommand();

        when(reader.findById(command.reservationId())).thenReturn(Optional.of(reservation));
        when(store.save(reservation)).thenReturn(reservation);

        var result = useCase.use(command);

        assertThat(result.reservationId()).isEqualTo(command.reservationId());
        assertThat(result.userId()).isEqualTo(command.requestedUser().subject());
        assertThat(result.state().status()).isEqualTo(ReservationStatus.USED);
        assertThat(result.state().usedAt()).isEqualTo(NOW);

        var captor = ArgumentCaptor.forClass(reservation.getClass());
        verify(store).save(captor.capture());
        assertThat(captor.getValue().getState().getStatus()).isEqualTo(ReservationStatus.USED);
        assertThat(captor.getValue().getState().getUsedAt()).isEqualTo(NOW);
        verify(reader).findById(command.reservationId());
        verifyNoMoreInteractions(reader, store);
    }

    @Test
    @DisplayName("예약을 찾을 수 없으면 RESERVATION_NOT_FOUND 예외")
    void throw_exception_when_reservation_not_found() {
        var command = useReservationCommand();

        when(reader.findById(command.reservationId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.use(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND);

        verify(reader, only()).findById(command.reservationId());
        verifyNoInteractions(store);
    }

    @Test
    @DisplayName("예약 소유자가 아니면 정책 거절 예외")
    void throw_exception_when_requester_is_not_reservation_owner() {
        var reservation = reservationWithId();
        var command = useReservationCommand(OTHER_ACTOR);

        when(reader.findById(command.reservationId())).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> useCase.use(command))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reasonCode", "reasonMessage")
                .containsExactly(
                        ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS.code(),
                        ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS.message()
                );

        assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.RESERVED);
        verify(reader, only()).findById(command.reservationId());
        verifyNoInteractions(store);
    }
}
