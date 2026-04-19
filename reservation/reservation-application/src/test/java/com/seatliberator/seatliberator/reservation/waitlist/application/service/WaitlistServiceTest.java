package com.seatliberator.seatliberator.reservation.waitlist.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationSeatOverlapCriteria;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.WaitlistStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.Waitlist;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.in.command.CreateWaitlistCommand;
import com.seatliberator.seatliberator.reservation.waitlist.application.port.out.WaitlistStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.WaitlistApplicationFixture.createWaitlistCancelCommand;
import static com.seatliberator.seatliberator.reservation.WaitlistApplicationFixture.createWaitlistCreateCommand;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static com.seatliberator.seatliberator.reservation.domain.fixture.WaitlistFixture.createWaitlist;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Waitlist Service")
public class WaitlistServiceTest {
    @Mock
    WaitlistStore store;

    @Mock
    ReservationReader reader;

    WaitlistService service;

    @BeforeEach
    void setup() {
        service = new WaitlistService(store, reader, fixedClock);
    }

    @Test
    @DisplayName("대기열 대상 좌석 및 시간에 예약이 존재하지 않으면 RESERVATION_NOT_FOUND 예외를 던진다")
    void throw_exception_when_reservation_not_found_in_locator_and_range() {
        var command = createWaitlistCreateCommand();

        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var range = SimpleTimeRange.of(command.startTime(), command.endTime());

        var criteria = ReservationSeatOverlapCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        when(reader.existsOverlapping(criteria)).thenReturn(false);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 동일 시간 / 좌석에 대기열 요청이 존재하면 중복 요청 시 DUPLICATED_REQUEST 예외를 던진다.")
    void throw_exception_when_active_request_already_exists() {
        var command = createWaitlistCreateCommand();
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var range = SimpleTimeRange.of(command.startTime(), command.endTime());

        var criteria = ReservationSeatOverlapCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        when(reader.existsOverlapping(criteria)).thenReturn(true);

        whenActiveWaitlistExists(command, true);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.DUPLICATED_REQUEST);
    }

    @Test
    @DisplayName("활성 요청이 없으면 요청을 생성하고 저장한다")
    void save_request_when_no_active_request_exists() {
        var command = createWaitlistCreateCommand();
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var range = SimpleTimeRange.of(command.startTime(), command.endTime());

        var criteria = ReservationSeatOverlapCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        when(reader.existsOverlapping(criteria)).thenReturn(true);

        whenActiveWaitlistExists(command, false);

        when(store.save(any(Waitlist.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(command);

        assertThat(result).isNotNull();
        verify(store).save(any(Waitlist.class));
    }

    @Test
    @DisplayName("저장 시 무결성 예외가 발생하면 DUPLICATED_REQUEST 예외로 변환한다")
    void throw_duplicated_request_when_save_raises_data_integrity_violation() {
        var command = createWaitlistCreateCommand();
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var range = SimpleTimeRange.of(command.startTime(), command.endTime());

        var criteria = ReservationSeatOverlapCriteria.of(locator, range)
                .withFilter(ReservationFilter.empty().withStatuses(ReservationStatus.RESERVED));
        when(reader.existsOverlapping(criteria)).thenReturn(true);
        whenActiveWaitlistExists(command, false);
        when(store.save(any(Waitlist.class)))
                .thenThrow(new DataIntegrityViolationException("duplicated request"));

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.DUPLICATED_REQUEST);
    }

    @Test
    @DisplayName("본인 요청이면 대기열 요청을 취소하고 저장한다")
    void cancel_request_when_command_user_is_owner() {
        var request = createWaitlist();
        var requestId = UUID.randomUUID();
        var command = createWaitlistCancelCommand(request.getUserId(), requestId);

        when(store.findById(requestId)).thenReturn(Optional.of(request));

        service.cancel(command);

        assertThat(request.getState().getStatus()).isEqualTo(WaitlistStatus.CANCELLED);
        verify(store).save(request);
    }

    @Test
    @DisplayName("취소 대상이 없으면 NOT_FOUND 예외를 던진다")
    void throw_not_found_when_cancel_target_missing() {
        var requestId = UUID.randomUUID();
        var command = createWaitlistCancelCommand(requestId);

        when(store.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.NOT_FOUND);

        verify(store, never()).save(any(Waitlist.class));
    }

    @Test
    @DisplayName("본인 요청이 아니면 UNAUTHORIZED_CANCELLATION 예외를 던진다")
    void throw_unauthorized_cancellation_when_command_user_is_not_owner() {
        var request = createWaitlist();
        var requestId = UUID.randomUUID();
        var command = createWaitlistCancelCommand("other-" + request.getUserId(), requestId);

        when(store.findById(requestId)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.cancel(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.UNAUTHORIZED_CANCELLATION);

        verify(store, never()).save(any(Waitlist.class));
    }

    private void whenActiveWaitlistExists(CreateWaitlistCommand command, boolean value) {
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var range = SimpleTimeRange.of(command.startTime(), command.endTime());
        when(store.existsByUserIdAndLocatorAndRangeAndStatus(command.userId(), locator, range, WaitlistStatus.ACTIVE))
                .thenReturn(value);
    }
}
