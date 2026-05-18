package com.seatliberator.seatliberator.reservation.web.reservation;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.FindMyReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.FindMyReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleInstantRange;
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
import java.util.Set;

import static com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture.createReservation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
    ActorContextHolder actorContextHolder;
    @MockitoBean
    FindMyReservationUseCase findMyReservationUseCase;

    @Test
    @DisplayName("내 예약 조회 요청 시 요청 파라미터와 actor 정보를 기반으로 query를 만들어서 유스케이스에 전달한다")
    void me_build_query_and_calls_use_case() throws Exception {
        // given
        var actor = new SimpleActor("user-1", Set.of());
        var startAt = Instant.parse("2026-04-14T10:00:00Z");
        var endAt = Instant.parse("2026-04-14T12:00:00Z");

        given(actorContextHolder.getActor()).willReturn(actor);
        given(findMyReservationUseCase.find(any(FindMyReservationQuery.class)))
                .willReturn(List.of());

        // when
        mockMvc.perform(get("/reservations/me")
                .queryParam("start", startAt.toString())
                .queryParam("end", endAt.toString())
                .queryParam("status", ReservationStatus.RESERVED.name())
        ).andExpect(status().isOk());

        // then
        var captor = ArgumentCaptor.forClass(FindMyReservationQuery.class);
        verify(findMyReservationUseCase).find(captor.capture());

        var actual = captor.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.userId()).isEqualTo("user-1");
        assertThat(actual.range()).isEqualTo(SimpleInstantRange.of(startAt, endAt));
        assertThat(actual.status()).isEqualTo(ReservationStatus.RESERVED);
    }

    @Test
    @DisplayName("내 예약 조회 요청 시 유스케이스 결과를 200 OK 응답으로 반환한다")
    void me_returns_ok_with_result() throws Exception {
        // given
        var actor = new SimpleActor("user-1", Set.of());
        var startAt = Instant.parse("2026-04-14T10:00:00Z");
        var endAt = Instant.parse("2026-04-14T12:00:00Z");

        var result = List.of(ReservationResult.from(createReservation(startAt, endAt, ReservationStatus.RESERVED)));

        given(actorContextHolder.getActor()).willReturn(actor);
        given(findMyReservationUseCase.find(any(FindMyReservationQuery.class)))
                .willReturn(result);

        // when & then
        mockMvc.perform(get("/reservations/me")
                        .queryParam("start", startAt.toString())
                        .queryParam("end", endAt.toString())
                        .queryParam("status", ReservationStatus.RESERVED.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    @DisplayName("status 요청 파라미터가 잘못되면 400 Bad Request를 반환한다")
    void me_returns_bad_request_when_status_is_invalid() throws Exception {
        // given
        var actor = new SimpleActor("user-1", Set.of());
        var startAt = Instant.parse("2026-04-14T10:00:00Z");
        var endAt = Instant.parse("2026-04-14T12:00:00Z");

        given(actorContextHolder.getActor()).willReturn(actor);

        // when & then
        mockMvc.perform(get("/reservations/me")
                        .queryParam("start", startAt.toString())
                        .queryParam("end", endAt.toString())
                        .queryParam("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("start 요청 파라미터 형식이 잘못되면 400 Bad Request를 반환한다")
    void me_returns_bad_request_when_start_is_invalid() throws Exception {
        // given
        var actor = new SimpleActor("user-1", Set.of());
        given(actorContextHolder.getActor()).willReturn(actor);

        // when & then
        mockMvc.perform(get("/reservations/me")
                        .queryParam("start", "not-a-date")
                        .queryParam("end", "2026-04-14T12:00:00Z")
                        .queryParam("status", ReservationStatus.RESERVED.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("필수 요청 파라미터가 누락되면 400 Bad Request를 반환한다")
    void me_returns_bad_request_when_required_parameter_is_missing() throws Exception {
        // given
        var actor = new SimpleActor("user-1", Set.of());
        given(actorContextHolder.getActor()).willReturn(actor);

        // when & then
        mockMvc.perform(get("/reservations/me")
                        .queryParam("start", "2026-04-14T10:00:00Z")
                        .queryParam("status", ReservationStatus.RESERVED.name()))
                .andExpect(status().isBadRequest());
    }
}
