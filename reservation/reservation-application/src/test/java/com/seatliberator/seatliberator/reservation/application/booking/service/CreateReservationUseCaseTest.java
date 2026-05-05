package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatePolicyCommand;
import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatorCommand;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyReason;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static com.seatliberator.seatliberator.reservation.ReservationTestSupport.createReservationCommand;
import static com.seatliberator.seatliberator.reservation.ReservationTestSupport.reservation;
import static com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture.createSeat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateReservationUseCase 테스트")
public class CreateReservationUseCaseTest extends AbstractReservationCommandServiceTest {
    @Test
    @DisplayName("예약 생성 시 좌석을 잠그고 ReservationCreator에 생성을 위임한다")
    void delegate_creation_to_reservation_creator_when_command_is_valid() {
        var command = createReservationCommand();
        var created = reservation();

        when(seatStore.findForUpdate(command.locator()))
                .thenReturn(Optional.of(createSeat()));
        when(reader.findByUserId(command.userId()))
                .thenReturn(Optional.empty());
        when(createAuthorizedPolicy.evaluate(command.requester()))
                .thenReturn(SimplePolicyResult.accept(ReservationPolicyReason.AUTHORIZED_RESERVATION_CREATE));
        when(createPolicy.evaluate(ReservationCreatePolicyCommand.from(command)))
                .thenReturn(SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_CREATABLE));
        when(creator.create(ReservationCreatorCommand.from(command)))
                .thenReturn(created);

        service.create(command);

        verify(seatStore).findForUpdate(command.locator());
        verify(reader).findByUserId(command.userId());
        verify(createAuthorizedPolicy).evaluate(command.requester());
        verify(createPolicy).evaluate(ReservationCreatePolicyCommand.from(command));
        verify(creator).create(ReservationCreatorCommand.from(command));
    }

    @Test
    @DisplayName("예약 대상 좌석이 없으면 SEAT_NOT_FOUND 예외를 던진다")
    void throw_exception_when_seat_not_found() {
        var command = createReservationCommand();

        when(seatStore.findForUpdate(command.locator()))
                .thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> service.create(command))
                .hasErrorCode(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        verify(store, never()).save(any());
        verify(createAuthorizedPolicy, never()).evaluate(any());
        verify(createPolicy, never()).evaluate(any());
        verify(creator, never()).create(any());
    }

    @Test
    @DisplayName("이미 사용자의 예약이 있으면 RESERVATION_ALREADY_EXISTS 예외를 던진다")
    void throw_exception_when_user_already_has_reservation() {
        var command = createReservationCommand();
        var existing = reservation();

        when(seatStore.findForUpdate(command.locator()))
                .thenReturn(Optional.of(createSeat()));
        when(reader.findByUserId(command.userId()))
                .thenReturn(Optional.of(existing));

        assertThatApplicationThrownBy(() -> service.create(command))
                .hasErrorCode(ReservationApplicationErrorCode.RESERVATION_ALREADY_EXISTS);

        verify(store, never()).save(any());
        verify(createAuthorizedPolicy, never()).evaluate(any());
        verify(createPolicy, never()).evaluate(any());
        verify(creator, never()).create(any());
    }

    @Test
    @DisplayName("예약 생성 권한 정책을 통과하지 못하면 예외를 던지고 예약 생성을 위임하지 않는다")
    void throw_exception_when_create_authorized_policy_rejects() {
        var command = createReservationCommand();

        when(seatStore.findForUpdate(command.locator()))
                .thenReturn(Optional.of(createSeat()));
        when(reader.findByUserId(command.userId()))
                .thenReturn(Optional.empty());
        when(createAuthorizedPolicy.evaluate(command.requester()))
                .thenReturn(SimplePolicyResult.reject(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_CREATE));

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("errorCode", "reasonCode")
                .containsExactly(
                        ReservationApplicationErrorCode.RESERVATION_POLICY_REJECTED,
                        ReservationPolicyReason.UNAUTHORIZED_RESERVATION_CREATE.code()
                );

        verify(createAuthorizedPolicy).evaluate(command.requester());
        verify(createPolicy, never()).evaluate(any());
        verify(creator, never()).create(any());
    }

    @Test
    @DisplayName("예약 생성 정책을 통과하지 못하면 예외를 던지고 예약 생성을 위임하지 않는다")
    void throw_exception_when_create_policy_rejects() {
        var command = createReservationCommand();

        when(seatStore.findForUpdate(command.locator()))
                .thenReturn(Optional.of(createSeat()));
        when(reader.findByUserId(command.userId()))
                .thenReturn(Optional.empty());
        when(createAuthorizedPolicy.evaluate(command.requester()))
                .thenReturn(SimplePolicyResult.accept(ReservationPolicyReason.AUTHORIZED_RESERVATION_CREATE));
        when(createPolicy.evaluate(ReservationCreatePolicyCommand.from(command)))
                .thenReturn(SimplePolicyResult.reject(RoomPolicyReason.OUT_OF_OPERATION_HOURS));

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ReservationApplicationPolicyException.class)
                .extracting("errorCode", "reasonCode")
                .containsExactly(
                        ReservationApplicationErrorCode.RESERVATION_POLICY_REJECTED,
                        RoomPolicyReason.OUT_OF_OPERATION_HOURS.code()
                );

        verify(createAuthorizedPolicy).evaluate(command.requester());
        verify(createPolicy).evaluate(ReservationCreatePolicyCommand.from(command));
        verify(creator, never()).create(any());
    }
}
