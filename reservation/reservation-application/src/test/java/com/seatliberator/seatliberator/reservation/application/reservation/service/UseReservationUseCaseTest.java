package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
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

import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UseReservationUseCase 테스트")
public class UseReservationUseCaseTest {
    @Mock
    ReservationReader reader;

    @Mock
    ReservationStore store;

    @Mock
    ActorContextHolder actorContextHolder;

    UseReservationUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new UseReservationService(reader, store, new ReservationOwnershipPolicy(reader), actorContextHolder, CLOCK);
    }

    @Test
    @DisplayName("예약 사용 시 현재 actor의 예약을 사용 상태로 변경하고 저장한다")
    void use_reservation() {
        var reservation = reservationWithId();
        var command = useReservationCommand();

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(reader.findById(command.reservationId())).thenReturn(Optional.of(reservation));
        when(store.save(reservation)).thenReturn(reservation);

        var result = useCase.use(command);

        assertThat(result.reservationId()).isEqualTo(command.reservationId());
        assertThat(result.userId()).isEqualTo(ACTOR.subject());
        assertThat(result.state().status()).isEqualTo(ReservationStatus.USED);
        assertThat(result.state().usedAt()).isEqualTo(NOW);

        var captor = ArgumentCaptor.forClass(reservation.getClass());
        verify(store).save(captor.capture());
        assertThat(captor.getValue().getState().getStatus()).isEqualTo(ReservationStatus.USED);
        assertThat(captor.getValue().getState().getUsedAt()).isEqualTo(NOW);
        verify(actorContextHolder).getActor();
        verify(reader).findById(command.reservationId());
        verifyNoMoreInteractions(actorContextHolder, reader, store);
    }

    @Test
    @DisplayName("예약을 찾을 수 없으면 RESERVATION_NOT_FOUND 예외")
    void throw_exception_when_reservation_not_found() {
        var command = useReservationCommand();

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(reader.findById(command.reservationId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.use(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND);

        verify(actorContextHolder).getActor();
        verify(reader, only()).findById(command.reservationId());
        verifyNoInteractions(store);
    }

    @Test
    @DisplayName("현재 actor가 예약 소유자가 아니면 정책 거절 예외")
    void throw_exception_when_requester_is_not_reservation_owner() {
        var reservation = reservationWithId();
        var command = useReservationCommand();

        when(actorContextHolder.getActor()).thenReturn(OTHER_ACTOR);
        when(reader.findById(command.reservationId())).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> useCase.use(command))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("reason")
                .isEqualTo(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);

        assertThat(reservation.getState().getStatus()).isEqualTo(ReservationStatus.RESERVED);
        verify(actorContextHolder).getActor();
        verify(reader, only()).findById(command.reservationId());
        verifyNoInteractions(store);
    }
}
