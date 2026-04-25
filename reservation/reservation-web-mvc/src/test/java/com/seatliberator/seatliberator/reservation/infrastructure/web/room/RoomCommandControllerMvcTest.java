package com.seatliberator.seatliberator.reservation.infrastructure.web.room;


import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.DeleteRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.UpdateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.DeleteRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.RoomResult;
import com.seatliberator.seatliberator.reservation.infrastructure.web.room.controller.RoomCommandController;
import com.seatliberator.seatliberator.reservation.infrastructure.web.room.request.CreateRoomRequest;
import com.seatliberator.seatliberator.reservation.infrastructure.web.room.request.UpdateRoomRequest;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@WebMvcTest(RoomCommandController.class)
@Import({RoomCommandControllerMvcTest.TestSecurityConfig.class})
@DisplayName("Room Command Controller MVC")
public class RoomCommandControllerMvcTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CreateRoomUseCase createRoomUseCase;

    @MockitoBean
    UpdateRoomUseCase updateRoomUseCase;

    @MockitoBean
    DeleteRoomUseCase deleteRoomUseCase;

    Clock clock = fixedClock;

    Instant now = clock.instant();

    @BeforeEach
    void run() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
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
    @DisplayName("방 생성")
    class CreateRoom {
        @Test
        @DisplayName("인증되지 않으면 방 생성 요청은 401")
        void create_room_unauthenticated() throws Exception {
            var request = new CreateRoomRequest("study-room-1");

            mockMvc.perform(post("/rooms")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(authorities = {"other.permission"})
        @DisplayName("room.manage 권한이 없으면 방 생성 요청은 403")
        void create_room_forbidden() throws Exception {
            var request = new CreateRoomRequest("study-room-1");

            mockMvc.perform(post("/rooms")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = {"room.manage"})
        @DisplayName("room.manage 권한이 있으면 방 생성 요청을 처리한다")
        void create_room_with_authority() throws Exception {
            var request = new CreateRoomRequest("study-room-1");
            var result = new RoomResult("study-room-1", now);

            when(createRoomUseCase.create(any(CreateRoomCommand.class))).thenReturn(result);

            mockMvc.perform(post("/rooms")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roomId").value("study-room-1"));

            var captor = ArgumentCaptor.forClass(CreateRoomCommand.class);
            verify(createRoomUseCase).create(captor.capture());
            assertThat(captor.getValue().roomId()).isEqualTo("study-room-1");
        }
    }

    @Nested
    @DisplayName("방 수정")
    class UpdateRoom {

        @Test
        @DisplayName("인증되지 않으면 401")
        void unauthorized() throws Exception {
            var request = new UpdateRoomRequest("new-room-1");

            mockMvc.perform(put("/rooms/{roomId}", "old-room-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(authorities = "other.permission")
        @DisplayName("room.manage 권한이 없으면 403")
        void forbidden() throws Exception {
            var request = new UpdateRoomRequest("new-room-1");

            mockMvc.perform(put("/rooms/{roomId}", "old-room-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(updateRoomUseCase, never()).update(any());
        }

        @Test
        @WithMockUser(authorities = "room.manage")
        @DisplayName("권한이 있으면 path variable과 요청 본문으로 수정 command를 만들어 유스케이스를 호출한다")
        void update_room() throws Exception {
            var request = new UpdateRoomRequest("new-room-1");
            var result = new RoomResult("new-room-1", now);

            when(updateRoomUseCase.update(any(UpdateRoomCommand.class)))
                    .thenReturn(result);

            mockMvc.perform(put("/rooms/{roomId}", "old-room-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            var captor = ArgumentCaptor.forClass(UpdateRoomCommand.class);
            verify(updateRoomUseCase).update(captor.capture());

            var command = captor.getValue();
            assertThat(command.oldRoomId()).isEqualTo("old-room-1");
            assertThat(command.newRoomId()).isEqualTo("new-room-1");
        }
    }

    @Nested
    @DisplayName("방 삭제")
    class DeleteRoom {

        @Test
        @DisplayName("인증되지 않으면 401")
        void unauthorized() throws Exception {
            mockMvc.perform(delete("/rooms/{roomId}", "study-room-1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(authorities = "other.permission")
        @DisplayName("room.manage 권한이 없으면 403")
        void forbidden() throws Exception {
            mockMvc.perform(delete("/rooms/{roomId}", "study-room-1"))
                    .andExpect(status().isForbidden());

            verify(deleteRoomUseCase, never()).delete(any());
        }

        @Test
        @WithMockUser(authorities = "room.manage")
        @DisplayName("권한이 있으면 path variable로 삭제 command를 만들어 유스케이스를 호출한다")
        void delete_room() throws Exception {
            mockMvc.perform(delete("/rooms/{roomId}", "study-room-1"))
                    .andExpect(status().isNoContent());

            var captor = ArgumentCaptor.forClass(DeleteRoomCommand.class);
            verify(deleteRoomUseCase).delete(captor.capture());

            var command = captor.getValue();
            assertThat(command.roomId()).isEqualTo("study-room-1");
        }
    }
}