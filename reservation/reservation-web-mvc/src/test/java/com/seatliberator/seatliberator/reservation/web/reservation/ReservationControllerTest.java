package com.seatliberator.seatliberator.reservation.web.reservation;

import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.FindReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.FindReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.web.reservation.controller.ReservationQueryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationQueryController.class)
public class ReservationControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    FindReservationUseCase findReservationUseCase;

    UuidGenerator uuid = new UuidGenerator(new SequenceCounter());

    @Test
    @DisplayName("예약 조회 요청 시 path variable을 기반으로 query를 만들어서 유스케이스에 전달한다")
    void find_build_query_and_calls_use_case() throws Exception {
        // given
        var reservationId = uuid.generate();
        var result = reservationResult(reservationId);

        given(findReservationUseCase.find(any(FindReservationQuery.class)))
                .willReturn(result);

        // when
        mockMvc.perform(get("/reservations/{reservationId}", reservationId))
                .andExpect(status().isOk());

        // then
        var captor = ArgumentCaptor.forClass(FindReservationQuery.class);
        verify(findReservationUseCase).find(captor.capture());

        var actual = captor.getValue();
        assertThat(actual.reservationId()).isEqualTo(reservationId);
    }

    @Test
    @DisplayName("예약 조회 요청 시 유스케이스 결과를 200 OK 응답으로 반환한다")
    void find_returns_ok_with_result() throws Exception {
        // given
        var reservationId = uuid.generate();
        var result = reservationResult(reservationId);

        given(findReservationUseCase.find(any(FindReservationQuery.class)))
                .willReturn(result);

        // when & then
        mockMvc.perform(get("/reservations/{reservationId}", reservationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    @DisplayName("reservationId path variable 형식이 잘못되면 400 Bad Request를 반환한다")
    void find_returns_bad_request_when_reservation_id_is_invalid() throws Exception {
        // when & then
        mockMvc.perform(get("/reservations/{reservationId}", "not-a-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(findReservationUseCase);
    }

    private ReservationResult reservationResult(UUID reservationId) {
        var reservedAt = Instant.parse("2026-04-14T09:00:00Z");

        return new ReservationResult(
                reservationId,
                "user-1",
                new ReservationResult.ReservationStateResult(
                        ReservationStatus.RESERVED,
                        reservedAt,
                        null,
                        null,
                        null
                )
        );
    }
}
