package com.seatliberator.seatliberator.reservation.web.reservation;

import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.FindReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.ListReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.FindReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.ListReservationQuery;
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
import java.util.List;
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
    ListReservationUseCase listReservationUseCase;
    @MockitoBean
    FindReservationUseCase findReservationUseCase;

    UuidGenerator uuid = new UuidGenerator(new SequenceCounter());

    @Test
    @DisplayName("예약 목록 조회 요청 시 요청 파라미터를 기반으로 query를 만들어서 유스케이스에 전달한다")
    void list_build_query_and_calls_use_case() throws Exception {
        // given
        var userId = "user-1";
        var status = ReservationStatus.RESERVED;

        given(listReservationUseCase.list(any(ListReservationQuery.class)))
                .willReturn(List.of());

        // when
        mockMvc.perform(get("/reservations")
                        .queryParam("userId", userId)
                        .queryParam("status", status.name()))
                .andExpect(status().isOk());

        // then
        var captor = ArgumentCaptor.forClass(ListReservationQuery.class);
        verify(listReservationUseCase).list(captor.capture());

        var actual = captor.getValue();
        assertThat(actual.userId()).isEqualTo(userId);
        assertThat(actual.status()).isEqualTo(status);
    }

    @Test
    @DisplayName("예약 목록 조회 요청 시 유스케이스 결과를 200 OK 응답으로 반환한다")
    void list_returns_ok_with_result() throws Exception {
        // given
        var userId = "user-1";
        var status = ReservationStatus.RESERVED;
        var result = List.of(reservationResult(uuid.generate()));

        given(listReservationUseCase.list(any(ListReservationQuery.class)))
                .willReturn(result);

        // when & then
        mockMvc.perform(get("/reservations")
                        .queryParam("userId", userId)
                        .queryParam("status", status.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    @DisplayName("예약 목록 조회 요청 파라미터가 누락되면 400 Bad Request를 반환한다")
    void list_returns_bad_request_when_required_parameter_is_missing() throws Exception {
        // when & then
        mockMvc.perform(get("/reservations")
                        .queryParam("userId", "user-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(listReservationUseCase);
    }

    @Test
    @DisplayName("status 요청 파라미터 형식이 잘못되면 400 Bad Request를 반환한다")
    void list_returns_bad_request_when_status_is_invalid() throws Exception {
        // when & then
        mockMvc.perform(get("/reservations")
                        .queryParam("userId", "user-1")
                        .queryParam("status", "not-a-status"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(listReservationUseCase);
    }

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
