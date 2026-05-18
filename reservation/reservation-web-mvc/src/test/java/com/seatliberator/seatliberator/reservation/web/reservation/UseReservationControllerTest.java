package com.seatliberator.seatliberator.reservation.web.reservation;

import com.seatliberator.seatliberator.kernel.test.SequenceCounter;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.UseReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.web.reservation.controller.UseReservationController;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UseReservationController.class)
public class UseReservationControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    UseReservationUseCase useReservationUseCase;

    UuidGenerator uuid = new UuidGenerator(new SequenceCounter());

    @Test
    @DisplayName("예약 사용 요청 시 path variable을 기반으로 command를 만들어서 유스케이스에 전달한다")
    void use_build_command_and_calls_use_case() throws Exception {
        // given
        var reservationId = uuid.generate();
        var result = usedReservationResult(reservationId);

        given(useReservationUseCase.use(any(UseReservationCommand.class)))
                .willReturn(result);

        // when
        mockMvc.perform(post("/reservations/{reservationId}", reservationId))
                .andExpect(status().isOk());

        // then
        var captor = ArgumentCaptor.forClass(UseReservationCommand.class);
        verify(useReservationUseCase).use(captor.capture());

        var actual = captor.getValue();
        assertThat(actual.reservationId()).isEqualTo(reservationId);
    }

    @Test
    @DisplayName("예약 사용 요청 시 유스케이스 결과를 200 OK 응답으로 반환한다")
    void use_returns_ok_with_result() throws Exception {
        // given
        var reservationId = uuid.generate();
        var result = usedReservationResult(reservationId);

        given(useReservationUseCase.use(any(UseReservationCommand.class)))
                .willReturn(result);

        // when & then
        mockMvc.perform(post("/reservations/{reservationId}", reservationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    @DisplayName("reservationId path variable 형식이 잘못되면 400 Bad Request를 반환한다")
    void use_returns_bad_request_when_reservation_id_is_invalid() throws Exception {
        // when & then
        mockMvc.perform(post("/reservations/{reservationId}", "not-a-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(useReservationUseCase);
    }

    private ReservationResult usedReservationResult(UUID reservationId) {
        var reservedAt = Instant.parse("2026-04-14T09:00:00Z");
        var usedAt = Instant.parse("2026-04-14T10:00:00Z");

        return new ReservationResult(
                reservationId,
                "user-1",
                new ReservationResult.ReservationStateResult(
                        ReservationStatus.USED,
                        reservedAt,
                        usedAt,
                        null,
                        null
                )
        );
    }
}
