package com.seatliberator.seatliberator.reservation.application.usage.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorFixture;
import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.usage.port.in.UseReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.usage.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Use Reservation UseCase")
public class UseReservationUseCaseTest {
    @Mock
    ReservationReader reader;

    @Mock
    ReservationOwnershipPolicy ownershipPolicy;

    UseReservationUseCase useCase;

    UuidGenerator uuid = new UuidGenerator(new SequenceCounter());

    @BeforeEach
    void run() {
        useCase = new ReservationUsageService(reader, ownershipPolicy, fixedClock);
    }

    @Test
    @DisplayName("예약 소유권 정책이 승인하면 예약을 사용 처리하고 승인 결과를 반환한다")
    void use_reservation_when_ownership_policy_accepts() {
        var reservationId = uuid.generate();
        var actor = new ActorFixture.Builder().subject("user-1").build();
        var command = new UseReservationCommand(reservationId, actor);
        var reservation = new ReservationFixture.Builder()
                .userId(actor.subject())
                .startAt(fixedClock.instant().minusSeconds(60))
                .endAt(fixedClock.instant().plusSeconds(60))
                .build();

        when(reader.findById(command.reservationId()))
                .thenReturn(Optional.of(reservation));
        when(ownershipPolicy.evaluate(reservation, actor))
                .thenReturn(ReservationPolicyResult.accept(ReservationPolicyReason.RESERVATION_OWNER));

        var result = useCase.use(command);

        assertThat(result.accept()).isTrue();
        assertThat(result.rejectReason()).isNull();
        assertThat(result.processedAt()).isEqualTo(fixedClock.instant());
        assertThat(reservation.isUsed()).isTrue();

        verify(reader).findById(command.reservationId());
        verify(ownershipPolicy).evaluate(reservation, actor);
    }

    @Test
    @DisplayName("예약 소유권 정책이 거절하면 예약을 사용 처리하지 않고 거절 결과를 반환한다")
    void reject_use_reservation_when_ownership_policy_rejects() {
        var reservationId = uuid.generate();
        var actor = new ActorFixture.Builder().subject("user-2").build();
        var command = new UseReservationCommand(reservationId, actor);
        var reservation = new ReservationFixture.Builder()
                .userId("user-1")
                .startAt(fixedClock.instant().minusSeconds(60))
                .endAt(fixedClock.instant().plusSeconds(60))
                .build();
        var reason = ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS;

        when(reader.findById(command.reservationId()))
                .thenReturn(Optional.of(reservation));
        when(ownershipPolicy.evaluate(reservation, actor))
                .thenReturn(ReservationPolicyResult.reject(reason));

        var result = useCase.use(command);

        assertThat(result.accept()).isFalse();
        assertThat(result.rejectReason()).isEqualTo(reason.message());
        assertThat(result.processedAt()).isEqualTo(fixedClock.instant());
        assertThat(reservation.isReserved()).isTrue();

        verify(reader).findById(command.reservationId());
        verify(ownershipPolicy).evaluate(reservation, actor);
    }

    @Test
    @DisplayName("예약이 없으면 RESERVATION_NOT_FOUND 예외를 던진다")
    void throw_exception_when_reservation_not_found() {
        var reservationId = uuid.generate();
        var actor = new ActorFixture.Builder().subject("user-1").build();
        var command = new UseReservationCommand(reservationId, actor);

        when(reader.findById(command.reservationId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.use(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND);

        verify(reader).findById(command.reservationId());
        verifyNoInteractions(ownershipPolicy);
    }
}
