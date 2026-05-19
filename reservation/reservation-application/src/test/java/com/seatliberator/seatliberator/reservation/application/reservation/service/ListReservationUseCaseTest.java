package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationReadAuthorizer;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.ListReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.ListReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter.ReservationFilter;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListReservationUseCase 테스트")
public class ListReservationUseCaseTest {
    @Mock
    ReservationReader reader;
    @Mock
    ReservationReadAuthorizer authorizer;
    @Mock
    ActorContextHolder actorContextHolder;

    ListReservationUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new ListReservationService(reader, authorizer, actorContextHolder);
    }

    @Test
    @DisplayName("예약 목록 조회 시 현재 actor의 조회 권한을 검증하고 필터 조회 결과를 ReservationResult로 반환한다")
    void list_reservations_validates_actor_and_returns_results() {
        var reservation = reservation();
        var query = ListReservationQuery.of(USER_ID, ReservationStatus.RESERVED);
        var filter = ReservationFilter.empty()
                .userId(query.userId())
                .status(query.status());

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(reader.findByFilter(filter)).thenReturn(List.of(reservation));

        var result = useCase.list(query);

        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(ReservationResult.from(reservation));

        verify(actorContextHolder).getActor();
        verify(authorizer).validate(ACTOR);
        verify(reader).findByFilter(filter);
        verifyNoMoreInteractions(actorContextHolder, authorizer, reader);
    }

    @Test
    @DisplayName("조회 결과가 없으면 빈 리스트를 반환한다")
    void return_empty_list_when_reader_result_empty() {
        var query = ListReservationQuery.of(USER_ID, ReservationStatus.RESERVED);
        var filter = ReservationFilter.empty()
                .userId(query.userId())
                .status(query.status());

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        when(reader.findByFilter(filter)).thenReturn(List.of());

        var result = useCase.list(query);

        assertThat(result).isEmpty();
        verify(actorContextHolder).getActor();
        verify(authorizer).validate(ACTOR);
        verify(reader).findByFilter(filter);
        verifyNoMoreInteractions(actorContextHolder, authorizer, reader);
    }

    @Test
    @DisplayName("현재 actor에게 예약 조회 권한이 없으면 예약을 조회하지 않는다")
    void throw_exception_when_actor_has_no_read_capability() {
        var query = ListReservationQuery.of(USER_ID, ReservationStatus.RESERVED);
        var exception = new ReservationApplicationPolicyException(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);

        when(actorContextHolder.getActor()).thenReturn(ACTOR);
        doThrow(exception).when(authorizer).validate(ACTOR);

        assertThatThrownBy(() -> useCase.list(query))
                .isSameAs(exception);

        verify(actorContextHolder).getActor();
        verify(authorizer).validate(ACTOR);
        verifyNoInteractions(reader);
    }
}
