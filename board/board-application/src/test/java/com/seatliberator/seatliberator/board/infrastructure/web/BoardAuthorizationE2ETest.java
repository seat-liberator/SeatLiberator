package com.seatliberator.seatliberator.board.infrastructure.web;

import com.seatliberator.seatliberator.board.application.port.in.BoardManager;
import com.seatliberator.seatliberator.board.application.port.in.CategoryEntry;
import com.seatliberator.seatliberator.board.application.port.in.CategoryManager;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryCreateCommand;
import com.seatliberator.seatliberator.board.infrastructure.web.fixture.TestPayloadFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.seatliberator.seatliberator.board.infrastructure.security.BoardCapability.CATEGORY_MANAGE;
import static com.seatliberator.seatliberator.board.infrastructure.security.BoardCapability.POST_CREATE;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoardAuthorizationE2ETest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardManager boardManager;

    @Autowired
    private CategoryManager categoryManager;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private UUID boardId;
    private UUID categoryId;
    private UUID otherBoardCategoryId;

    private static final String TOKEN = "test-token-digest";
    private static final String SUBJECT = "test-user";

    @BeforeEach
    void setUp() {
        boardId = boardManager.create(new BoardCreateCommand("board-a", "desc")).boardId();

        var defaultCategoryName = "general-" + UUID.randomUUID();
        categoryManager.create(new CategoryCreateCommand(boardId, defaultCategoryName, "desc"));
        categoryId = categoryManager.getAll(boardId).stream()
                .filter(category -> defaultCategoryName.equals(category.name()))
                .map(CategoryEntry::categoryId)
                .findFirst()
                .orElseThrow();

        var otherBoardId = boardManager.create(new BoardCreateCommand("board-b", "desc")).boardId();
        var otherCategoryName = "other-" + UUID.randomUUID();
        categoryManager.create(new CategoryCreateCommand(otherBoardId, otherCategoryName, "desc"));
        otherBoardCategoryId = categoryManager.getAll(otherBoardId).stream()
                .filter(category -> otherCategoryName.equals(category.name()))
                .map(CategoryEntry::categoryId)
                .findFirst()
                .orElseThrow();
    }

    @Test
    void shouldReturn403WhenCreatingCategoryWithoutCategoryManageAuthority() throws Exception {
        mockMvc.perform(post("/board/{boardId}/categories", boardId)
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"news\",\"description\":\"desc\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn201WhenCreatingCategoryWithCategoryManageAuthority() throws Exception {
        mockMvc.perform(post("/board/{boardId}/categories", boardId)
                        .with(auth(CATEGORY_MANAGE.scope()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"news\",\"description\":\"desc\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn403WhenCreatingPostWithoutPostCreateAuthority() throws Exception {
        mockMvc.perform(post("/board/{boardId}/posts", boardId)
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postCreateBody(categoryId)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn201WhenCreatingPostWithPostCreateAuthority() throws Exception {
        mockMvc.perform(post("/board/{boardId}/posts", boardId)
                        .with(auth(POST_CREATE.scope()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postCreateBody(categoryId)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn404WhenCreatingPostWithNonExistingCategoryId() throws Exception {
        mockMvc.perform(post("/board/{boardId}/posts", boardId)
                        .with(auth(POST_CREATE.scope()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postCreateBody(UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenCreatingPostWithCategoryFromAnotherBoard() throws Exception {
        mockMvc.perform(post("/board/{boardId}/posts", boardId)
                        .with(auth(POST_CREATE.scope()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postCreateBody(otherBoardCategoryId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("board:USER 스코프는 게시글 작성을 할 수 있다")
    void USER_role_can_post() throws Exception {
        given(jwtDecoder.decode(TOKEN))
                .willReturn(mockJwt(SUBJECT, List.of("board:USER")));

        var payload = TestPayloadFixture.createPostPayload(categoryId.toString());

        mockMvc.perform(
                post("/board/{boardId}/posts", boardId)
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestPayloadFixture.stringifyPayload(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("board:USER 스코프는 카테고리 생성 및 관리를 할 수 없다")
    void USER_role_can_not_manage_category() throws Exception {
        given(jwtDecoder.decode(TOKEN))
                .willReturn(mockJwt(SUBJECT, List.of("board:USER")));

        var payload = TestPayloadFixture.createCategoryPayload();

        mockMvc.perform(
                post("/board/{boardId}/categories", boardId)
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestPayloadFixture.stringifyPayload(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("board:USER 스코프와 유효하지 않은 스코프가 함께 들어와도 게시글을 작성할 수 있다")
    void USER_role_can_post_with_invalid_scope() throws Exception {
        given(jwtDecoder.decode(TOKEN))
                .willReturn(mockJwt(SUBJECT, List.of("board:USER", "boom")));

        var payload = TestPayloadFixture.createPostPayload(categoryId.toString());

        mockMvc.perform(
                post("/board/{boardId}/posts", boardId)
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestPayloadFixture.stringifyPayload(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("board:MAINTAINER 스코프는 카테고리 생성 및 관리를 할 수 있다")
    void MAINTAINER_role_can_manage_category() throws Exception {
        given(jwtDecoder.decode(TOKEN))
                .willReturn(mockJwt(SUBJECT, List.of("board:MAINTAINER")));

        var payload = TestPayloadFixture.createCategoryPayload();

        mockMvc.perform(
                post("/board/{boardId}/categories", boardId)
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestPayloadFixture.stringifyPayload(payload)))
                .andExpect(status().isCreated());
    }

    private String postCreateBody(UUID requestCategoryId) {
        return """
                {"categoryId":"%s","title":"title","content":"content"}
                """.formatted(requestCategoryId);
    }

    private Jwt mockJwt(String subject, List<String> scopes) {
        return Jwt.withTokenValue(TOKEN)
                .header("alg", "none")
                .subject(subject)
                .claim("scopes", scopes)
                .build();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor auth(String... authorities) {
        var requestPostProcessor = jwt().jwt(token -> token.subject("test-user"));
        if (authorities.length == 0) {
            return requestPostProcessor.authorities();
        }
        return requestPostProcessor.authorities(
                java.util.Arrays.stream(authorities)
                        .map(SimpleGrantedAuthority::new)
                        .toArray(SimpleGrantedAuthority[]::new)
        );
    }
}
