package com.seatliberator.seatliberator.reservation.web.seat;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.FindSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.ListSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatStatus;
import com.seatliberator.seatliberator.reservation.web.seat.controller.SeatQueryController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.shared.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeatQueryController.class)
@Import({SeatQueryControllerMvcTest.TestSecurityConfig.class})
@DisplayName("Seat Query Controller MVC")
public class SeatQueryControllerMvcTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @MockitoBean
    ListSeatUseCase listSeatUseCase;

    @MockitoBean
    FindSeatUseCase findSeatUseCase;

    Clock clock = fixedClock;

    Instant now = clock.instant();

    @BeforeEach
    void run() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .httpBasic(Customizer.withDefaults())
                    .build();
        }
    }

    @Nested
    @DisplayName("좌석 목록 조회")
    class ListSeat {
        @Test
        @DisplayName("인증되지 않으면 401")
        void unauthorized() throws Exception {
            mockMvc.perform(get("/rooms/{roomId}/seats", "study-room-1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(authorities = "other.permission")
        @DisplayName("seat.list 권한이 없으면 403")
        void forbidden() throws Exception {
            mockMvc.perform(get("/rooms/{roomId}/seats", "study-room-1"))
                    .andExpect(status().isForbidden());

            verify(listSeatUseCase, never()).list(any());
        }

        @Test
        @WithMockUser(authorities = "seat.list")
        @DisplayName("seat.list 권한이 있으면 path variable로 query를 만들어 유스케이스를 호출한다")
        void list_seats() throws Exception {
            var result = List.of(new SeatResult("seat-a", now, SeatStatus.ACTIVE, null, null));
            when(listSeatUseCase.list(any(ListSeatQuery.class))).thenReturn(result);

            mockMvc.perform(get("/rooms/{roomId}/seats", "study-room-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].seatId").value("seat-a"));

            var captor = ArgumentCaptor.forClass(ListSeatQuery.class);
            verify(listSeatUseCase).list(captor.capture());
            assertThat(captor.getValue().roomId()).isEqualTo("study-room-1");
        }
    }

    @Nested
    @DisplayName("좌석 단건 조회")
    class FindSeat {
        @Test
        @DisplayName("인증되지 않으면 401")
        void unauthorized() throws Exception {
            mockMvc.perform(get("/rooms/{roomId}/seats/{seatId}", "study-room-1", "seat-a"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(authorities = "other.permission")
        @DisplayName("seat.read 권한이 없으면 403")
        void forbidden() throws Exception {
            mockMvc.perform(get("/rooms/{roomId}/seats/{seatId}", "study-room-1", "seat-a"))
                    .andExpect(status().isForbidden());

            verify(findSeatUseCase, never()).find(any());
        }

        @Test
        @WithMockUser(authorities = "seat.read")
        @DisplayName("seat.read 권한이 있으면 path variable로 query를 만들어 유스케이스를 호출한다")
        void find_seat() throws Exception {
            when(findSeatUseCase.find(any(FindSeatQuery.class)))
                    .thenReturn(new SeatResult("seat-a", now, SeatStatus.ACTIVE, null, null));

            mockMvc.perform(get("/rooms/{roomId}/seats/{seatId}", "study-room-1", "seat-a"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.seatId").value("seat-a"));

            var captor = ArgumentCaptor.forClass(FindSeatQuery.class);
            verify(findSeatUseCase).find(captor.capture());

            var query = captor.getValue();
            assertThat(query.roomId()).isEqualTo("study-room-1");
            assertThat(query.seatId()).isEqualTo("seat-a");
        }
    }
}
