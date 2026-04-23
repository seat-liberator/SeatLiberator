package com.seatliberator.seatliberator.reservation.room.infrastructure;

import com.seatliberator.seatliberator.reservation.room.application.port.in.FindRoomUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.ListRoomUseCase;
import com.seatliberator.seatliberator.reservation.room.application.port.in.query.FindRoomQuery;
import com.seatliberator.seatliberator.reservation.room.application.port.in.result.RoomResult;
import com.seatliberator.seatliberator.reservation.room.infrastructure.web.controller.RoomQueryController;
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

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomQueryController.class)
@Import({RoomQueryControllerMvcTest.TestSecurityConfig.class})
@DisplayName("Room Query Controller MVC")
public class RoomQueryControllerMvcTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @MockitoBean
    ListRoomUseCase listRoomUseCase;

    @MockitoBean
    FindRoomUseCase findRoomUseCase;

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
    @DisplayName("방 목록 조회")
    class ListRoom {
        @Test
        @DisplayName("인증되지 않으면 401")
        void unauthorized() throws Exception {
            mockMvc.perform(get("/rooms"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(authorities = "other.permission")
        @DisplayName("room.list 권한이 없으면 403")
        void forbidden() throws Exception {
            mockMvc.perform(get("/rooms"))
                    .andExpect(status().isForbidden());

            verify(listRoomUseCase, never()).list();
        }

        @Test
        @WithMockUser(authorities = "room.list")
        @DisplayName("room.list 권한이 있으면 방 목록을 조회한다")
        void list_rooms() throws Exception {
            var result = List.of(new RoomResult("study-room-1", now));
            when(listRoomUseCase.list()).thenReturn(result);

            mockMvc.perform(get("/rooms"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].roomId").value("study-room-1"));

            verify(listRoomUseCase).list();
        }
    }

    @Nested
    @DisplayName("방 단건 조회")
    class FindRoom {
        @Test
        @DisplayName("인증되지 않으면 401")
        void unauthorized() throws Exception {
            mockMvc.perform(get("/rooms/{roomId}", "study-room-1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(authorities = "other.permission")
        @DisplayName("room.read 권한이 없으면 403")
        void forbidden() throws Exception {
            mockMvc.perform(get("/rooms/{roomId}", "study-room-1"))
                    .andExpect(status().isForbidden());

            verify(findRoomUseCase, never()).find(any());
        }

        @Test
        @WithMockUser(authorities = "room.read")
        @DisplayName("room.read 권한이 있으면 path variable로 query를 만들어 유스케이스를 호출한다")
        void find_room() throws Exception {
            when(findRoomUseCase.find(any(FindRoomQuery.class)))
                    .thenReturn(new RoomResult("study-room-1", now));

            mockMvc.perform(get("/rooms/{roomId}", "study-room-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roomId").value("study-room-1"));

            var captor = ArgumentCaptor.forClass(FindRoomQuery.class);
            verify(findRoomUseCase).find(captor.capture());
            assertThat(captor.getValue().roomId()).isEqualTo("study-room-1");
        }
    }
}
