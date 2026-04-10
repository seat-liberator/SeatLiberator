package com.seatliberator.seatliberator.reservation.vacancy.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationExistenceChecker;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCreateCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.VacancyAlertRequestApplicationFixture.createVacancyAlertRequestCancelCommand;
import static com.seatliberator.seatliberator.reservation.VacancyAlertRequestApplicationFixture.createVacancyRequestCreateCommand;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.VacancyAlertRequestFixture.createRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Default Vacancy Alert Requester")
public class DefaultVacancyAlertRequestRequesterTest {
    @Mock
    VacancyAlertRequestStore store;

    @Mock
    ReservationExistenceChecker checker;

    DefaultVacancyAlertRequester requester;

    @BeforeEach
    void setup() {
        requester = new DefaultVacancyAlertRequester(checker, store, fixedClock);
    }

    @Test
    @DisplayName("알림 대상 좌석 및 시간에 예약이 존재하지 않으면 RESERVATION_NOT_FOUND 예외를 던진다")
    void throw_exception_when_reservation_not_found_in_locator_and_range() {
        var command = createVacancyRequestCreateCommand();
        
        var locator = SimpleSeatLocator.from(command.roomId(), command.seatId());
        var range = SimpleTimeRange.from(command.startTime(), command.endTime());

        when(checker.isExistsByLocatorAndRangeAndStatus(locator, range, ReservationStatus.RESERVED)).thenReturn(false);

        assertThatThrownBy(() -> requester.request(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 동일 시간 / 좌석에 알람 요청이 존재하면 중복 요청 시 DUPLICATED_REQUEST 예외를 던진다.")
    void throw_exception_when_active_request_already_exists() {
        var command = createVacancyRequestCreateCommand();
        var locator = SimpleSeatLocator.from(command.roomId(), command.seatId());
        var range = SimpleTimeRange.from(command.startTime(), command.endTime());

        when(checker.isExistsByLocatorAndRangeAndStatus(locator, range, ReservationStatus.RESERVED)).thenReturn(true);

        whenCheckAlertRequestExists(command, true);

        assertThatThrownBy(() -> requester.request(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.DUPLICATED_REQUEST);
    }

    @Test
    @DisplayName("활성 요청이 없으면 요청을 생성하고 저장한다")
    void save_request_when_no_active_request_exists() {
        var command = createVacancyRequestCreateCommand();
        var locator = SimpleSeatLocator.from(command.roomId(), command.seatId());
        var range = SimpleTimeRange.from(command.startTime(), command.endTime());

        when(checker.isExistsByLocatorAndRangeAndStatus(locator, range, ReservationStatus.RESERVED)).thenReturn(true);

        whenCheckAlertRequestExists(command, false);

        when(store.save(any(VacancyAlertRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = requester.request(command);

        assertThat(result).isNotNull();
        verify(store).save(any(VacancyAlertRequest.class));
    }

    @Test
    @DisplayName("본인 요청이면 알림 요청을 취소하고 저장한다")
    void cancel_request_when_command_user_is_owner() {
        var request = createRequest();
        var requestId = UUID.randomUUID();
        var command = createVacancyAlertRequestCancelCommand(request.getUserId(), requestId);

        when(store.findById(requestId)).thenReturn(Optional.of(request));

        requester.cancel(command);

        assertThat(request.getState().getStatus()).isEqualTo(VacancyAlertRequestStatus.CANCELLED);
        verify(store).save(request);
    }

    @Test
    @DisplayName("취소 대상이 없으면 NOT_FOUND 예외를 던진다")
    void throw_not_found_when_cancel_target_missing() {
        var requestId = UUID.randomUUID();
        var command = createVacancyAlertRequestCancelCommand(requestId);

        when(store.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requester.cancel(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.NOT_FOUND);

        verify(store, never()).save(any(VacancyAlertRequest.class));
    }

    @Test
    @DisplayName("본인 요청이 아니면 UNAUTHORIZED_CANCELLATION 예외를 던진다")
    void throw_unauthorized_cancellation_when_command_user_is_not_owner() {
        var request = createRequest();
        var requestId = UUID.randomUUID();
        var command = createVacancyAlertRequestCancelCommand("other-" + request.getUserId(), requestId);

        when(store.findById(requestId)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> requester.cancel(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.UNAUTHORIZED_CANCELLATION);

        verify(store, never()).save(any(VacancyAlertRequest.class));
    }

    private void whenCheckAlertRequestExists(VacancyAlertRequestCreateCommand command, boolean value) {
        var locator = SimpleSeatLocator.from(command.roomId(), command.seatId());
        var range = SimpleTimeRange.from(command.startTime(), command.endTime());
        when(store.existsByUserIdAndLocatorAndRangeAndStatus(command.userId(), locator, range, VacancyAlertRequestStatus.ACTIVE))
                .thenReturn(value);
    }
}
