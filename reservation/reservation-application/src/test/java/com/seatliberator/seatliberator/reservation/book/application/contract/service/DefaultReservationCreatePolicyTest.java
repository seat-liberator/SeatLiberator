package com.seatliberator.seatliberator.reservation.book.application.contract.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreatePolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.booking.contract.service.DefaultReservationCreatePolicy;
import com.seatliberator.seatliberator.reservation.application.room.contract.RoomOperationReservationPolicy;
import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyReason;
import com.seatliberator.seatliberator.reservation.application.room.contract.result.RoomPolicyResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.ReservationTestSupport.reservationCreatePolicyCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationCreatePolicy 테스트")
public class DefaultReservationCreatePolicyTest {

    @Mock
    RoomOperationReservationPolicy roomOperationReservationPolicy;

    ReservationCreatePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new DefaultReservationCreatePolicy(roomOperationReservationPolicy);
    }

    @Test
    @DisplayName("Room 운영 예약 정책을 통과하면 승인한다")
    void accept_when_room_operation_reservation_policy_accepts() {
        var command = reservationCreatePolicyCommand();
        when(roomOperationReservationPolicy.evaluate(command.locator(), command.range()))
                .thenReturn(RoomPolicyResult.accept(RoomPolicyReason.ROOM_OPERATION_AVAILABLE));

        var result = policy.evaluate(command);

        assertThat(result.accepted()).isTrue();
        assertThat(result.reason()).isEqualTo(ReservationPolicyReason.RESERVATION_CREATABLE);
        verify(roomOperationReservationPolicy).evaluate(command.locator(), command.range());
    }

    @Test
    @DisplayName("Room 운영 예약 정책을 통과하지 못하면 거절 사유를 그대로 반환한다")
    void reject_with_room_policy_reason_when_room_operation_reservation_policy_rejects() {
        var command = reservationCreatePolicyCommand();
        when(roomOperationReservationPolicy.evaluate(command.locator(), command.range()))
                .thenReturn(RoomPolicyResult.reject(RoomPolicyReason.OUT_OF_OPERATION_HOURS));

        var result = policy.evaluate(command);

        assertThat(result.rejected()).isTrue();
        assertThat(result.reason()).isEqualTo(RoomPolicyReason.OUT_OF_OPERATION_HOURS);
        verify(roomOperationReservationPolicy).evaluate(command.locator(), command.range());
    }
}
