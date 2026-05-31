package com.seatliberator.seatliberator.reservation.web.booking;

import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindAvailableSlotsBySeatUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindAvailableSlotsBySeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDateRange;
import com.seatliberator.seatliberator.reservation.web.booking.controller.AvailabilityQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvailabilityQueryController.class)
public class AvailabilityQueryControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    FindAvailableSlotsBySeatUseCase findAvailableSlotsBySeatUseCase;

    UuidGenerator uuid = new UuidGenerator(new SequenceCounter());

    @Test
    @DisplayName("좌석 가용 슬롯 조회 요청 시 path variable과 요청 파라미터를 기반으로 query를 만들어서 유스케이스에 전달한다")
    void find_available_slots_build_query_and_calls_use_case() throws Exception {
        // given
        var seatId = uuid.generate();
        var startAt = LocalDate.of(2026, 5, 18);
        var endAt = LocalDate.of(2026, 5, 20);

        given(findAvailableSlotsBySeatUseCase.findAtDateRange(any(FindAvailableSlotsBySeatQuery.class)))
                .willReturn(Map.of());

        // when
        mockMvc.perform(get("/api/v1/booking/seats/{seatId}/available-slots", seatId)
                        .queryParam("start", startAt.toString())
                        .queryParam("end", endAt.toString()))
                .andExpect(status().isOk());

        // then
        var captor = ArgumentCaptor.forClass(FindAvailableSlotsBySeatQuery.class);
        verify(findAvailableSlotsBySeatUseCase).findAtDateRange(captor.capture());

        var actual = captor.getValue();
        assertThat(actual.seatId()).isEqualTo(seatId);
        assertThat(actual.range()).isEqualTo(SimpleDateRange.of(startAt, endAt));
    }

    @Test
    @DisplayName("좌석 가용 슬롯 조회 요청 시 유스케이스 결과를 200 OK 응답으로 반환한다")
    void find_available_slots_returns_ok_with_result() throws Exception {
        // given
        var seatId = uuid.generate();
        var startAt = LocalDate.of(2026, 5, 18);
        var endAt = LocalDate.of(2026, 5, 20);
        var result = availableSlotsResult(startAt);

        given(findAvailableSlotsBySeatUseCase.findAtDateRange(any(FindAvailableSlotsBySeatQuery.class)))
                .willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/booking/seats/{seatId}/available-slots", seatId)
                        .queryParam("start", startAt.toString())
                        .queryParam("end", endAt.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    @DisplayName("seatId path variable 형식이 잘못되면 400 Bad Request를 반환한다")
    void find_available_slots_returns_bad_request_when_seat_id_is_invalid() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/booking/seats/{seatId}/available-slots", "not-a-uuid")
                        .queryParam("start", "2026-05-18")
                        .queryParam("end", "2026-05-20"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(findAvailableSlotsBySeatUseCase);
    }

    @Test
    @DisplayName("날짜 요청 파라미터 형식이 잘못되면 400 Bad Request를 반환한다")
    void find_available_slots_returns_bad_request_when_date_is_invalid() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/booking/seats/{seatId}/available-slots", uuid.generate())
                        .queryParam("start", "not-a-date")
                        .queryParam("end", "2026-05-20"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(findAvailableSlotsBySeatUseCase);
    }

    private Map<LocalDate, List<SeatTimeSlotResult>> availableSlotsResult(LocalDate date) {
        var result = new LinkedHashMap<LocalDate, List<SeatTimeSlotResult>>();
        result.put(date, List.of(seatTimeSlotResult(uuid.generate())));
        return result;
    }

    private SeatTimeSlotResult seatTimeSlotResult(UUID slotId) {
        var createdAt = Instant.parse("2026-05-18T00:00:00Z");
        return new SeatTimeSlotResult(
                slotId,
                LocalTime.of(8, 0),
                Duration.ofHours(2),
                SeatTimeSlotStatus.ACTIVE,
                createdAt,
                createdAt,
                null
        );
    }
}
