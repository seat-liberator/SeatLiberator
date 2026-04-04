package com.seatliberator.seatliberator.reservation.book.application;

import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertStatus;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.command.VacancyAlertRequestCommand;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestReader;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.out.VacancyAlertRequestStore;
import com.seatliberator.seatliberator.reservation.vacancy.application.service.DefaultVacancyAlertRequester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.seatliberator.seatliberator.reservation.ReservationApplicationFixture.createVacancyAlertRequestCommand;
import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Default Vacancy Alert Requester")
public class DefaultVacancyAlertRequesterTest {
    @Mock
    VacancyAlertRequestReader reader;

    @Mock
    VacancyAlertRequestStore store;

    DefaultVacancyAlertRequester requester;

    @BeforeEach
    void setup() {
        requester = new DefaultVacancyAlertRequester(reader, store, fixedClock);
    }

    @Test
    @DisplayName("이미 동일 시간 / 좌석에 알람 요청이 존재하면 중복 요청 시 DUPLICATED_REQUEST 예외를 던진다.")
    void throw_exception_when_active_request_already_exists() {
        var requestedAt = fixedClock.instant();
        var command = createVacancyAlertRequestCommand(requestedAt);

        whenCheckAlertRequestExists(command, true);

        assertThatThrownBy(() -> requester.request(command))
                .isInstanceOf(ReservationApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationApplicationErrorCode.DUPLICATED_REQUEST);
    }

    @Test
    @DisplayName("활성 요청이 없으면 요청을 생성하고 저장한다")
    void save_request_when_no_active_request_exists() {
        var requestedAt = fixedClock.instant();
        var command = createVacancyAlertRequestCommand(requestedAt);

        whenCheckAlertRequestExists(command, false);

        when(store.save(any(VacancyAlertRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = requester.request(command);

        assertThat(result).isNotNull();
        verify(store).save(any(VacancyAlertRequest.class));
    }

    private void whenCheckAlertRequestExists(VacancyAlertRequestCommand command, boolean value) {
        var locator = SimpleSeatLocator.from(command.roomId(), command.seatId());
        var range = SimpleTimeRange.from(command.startTime(), command.endTime());
        when(reader.existsByUserIdAndLocatorAndRangeAndStatus(command.userId(), locator, range, VacancyAlertStatus.ACTIVE))
                .thenReturn(value);
    }
}
