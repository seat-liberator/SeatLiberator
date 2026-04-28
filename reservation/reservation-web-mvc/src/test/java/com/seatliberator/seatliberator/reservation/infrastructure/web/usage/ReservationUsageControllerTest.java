package com.seatliberator.seatliberator.reservation.infrastructure.web.usage;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActor;
import com.seatliberator.seatliberator.reservation.application.usage.port.in.UseReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.usage.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.usage.port.in.result.UseReservationResult;
import com.seatliberator.seatliberator.reservation.infrastructure.web.usage.controller.ReservationUsageController;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationUsageController.class)
public class ReservationUsageControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    ActorContextHolder actorContextHolder;
    @MockitoBean
    UseReservationUseCase useReservationUseCase;

    @Test
    @DisplayName("예약 사용 요청 시 path variable과 actor 정보를 기반으로 command를 만들어서 유스케이스에 전달한다")
    void use_build_command_and_calls_use_case() throws Exception {
        // given
        var actor = new SimpleActor("user-1", Set.of());
        var processedAt = Instant.parse("2026-04-14T10:00:00Z");
        var result = UseReservationResult.accept(processedAt);

        given(actorContextHolder.getActor()).willReturn(actor);
        given(useReservationUseCase.use(any(UseReservationCommand.class)))
                .willReturn(result);

        // when
        mockMvc.perform(post("/reservations/{reservationId}", 1L))
                .andExpect(status().isOk());

        // then
        var captor = ArgumentCaptor.forClass(UseReservationCommand.class);
        verify(useReservationUseCase).use(captor.capture());

        var actual = captor.getValue();
        assertThat(actual.reservationId()).isEqualTo(1L);
        assertThat(actual.requestedUser()).isEqualTo(actor);
    }

    @Test
    @DisplayName("예약 사용 요청 시 유스케이스 결과를 200 OK 응답으로 반환한다")
    void use_returns_ok_with_result() throws Exception {
        // given
        var actor = new SimpleActor("user-1", Set.of());
        var processedAt = Instant.parse("2026-04-14T10:00:00Z");
        var result = UseReservationResult.reject("해당 예약에 접근할 권한이 없습니다.", processedAt);

        given(actorContextHolder.getActor()).willReturn(actor);
        given(useReservationUseCase.use(any(UseReservationCommand.class)))
                .willReturn(result);

        // when & then
        mockMvc.perform(post("/reservations/{reservationId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    @DisplayName("reservationId path variable 형식이 잘못되면 400 Bad Request를 반환한다")
    void use_returns_bad_request_when_reservation_id_is_invalid() throws Exception {
        // when & then
        mockMvc.perform(post("/reservations/{reservationId}", "not-a-number"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(actorContextHolder);
        verifyNoInteractions(useReservationUseCase);
    }
}
