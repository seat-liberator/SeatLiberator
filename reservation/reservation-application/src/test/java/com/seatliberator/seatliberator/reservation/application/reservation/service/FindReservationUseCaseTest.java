package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationReadAuthorizer;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.FindReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.FindReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindReservationUseCase 테스트")
public class FindReservationUseCaseTest {
    @Mock
    ReservationReader reader;
    @Mock
    ReservationReadAuthorizer authorizer;
    @Mock
    ReservationOwnershipPolicy ownershipPolicy;
    @Mock
    ActorContextHolder actorContextHolder;

    FindReservationUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new FindReservationService(reader, authorizer, ownershipPolicy, actorContextHolder);
    }

    @Test
    @DisplayName("예약 ID 조회 시 현재 actor의 조회 권한과 소유권을 검증하고 ReservationResult를 반환한다")
    void find_by_reservation_id_validates_actor_and_returns_result() {
        var reservation = reservationWithId();
        var query = FindReservationQuery.of(RESERVATION_ID);

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(reader.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        var result = useCase.find(query);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(ReservationResult.from(reservation));

        verify(actorContextHolder).getActor();
        verify(authorizer).validate(ACTOR);
        verify(reader).findById(RESERVATION_ID);
        verify(ownershipPolicy).validate(reservation, ACTOR);
        verifyNoMoreInteractions(actorContextHolder, authorizer, reader, ownershipPolicy);
    }

    @Test
    @DisplayName("예약을 찾을 수 없으면 RESERVATION_NOT_FOUND 예외")
    void throw_exception_when_reservation_not_found() {
        var query = FindReservationQuery.of(RESERVATION_ID);

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(reader.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.find(query))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND);

        verify(actorContextHolder).getActor();
        verify(authorizer).validate(ACTOR);
        verify(reader, only()).findById(RESERVATION_ID);
        verifyNoInteractions(ownershipPolicy);
    }

    @Test
    @DisplayName("현재 actor에게 예약 조회 권한이 없으면 예약을 조회하지 않는다")
    void throw_exception_when_actor_has_no_read_capability() {
        var query = FindReservationQuery.of(RESERVATION_ID);
        var exception = new ReservationApplicationPolicyException(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        doThrow(exception).when(authorizer).validate(ACTOR);

        assertThatThrownBy(() -> useCase.find(query))
                .isSameAs(exception);

        verify(actorContextHolder).getActor();
        verify(authorizer).validate(ACTOR);
        verifyNoInteractions(reader, ownershipPolicy);
    }
}
