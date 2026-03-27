package com.seatliberator.seatliberator.board.infrastructure.web;

import com.seatliberator.seatliberator.board.application.port.in.BoardManager;
import com.seatliberator.seatliberator.board.application.port.in.CategoryEntry;
import com.seatliberator.seatliberator.board.application.port.in.CategoryManager;
import com.seatliberator.seatliberator.board.application.port.in.command.BoardCreateCommand;
import com.seatliberator.seatliberator.board.application.port.in.command.CategoryCreateCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.seatliberator.seatliberator.board.infrastructure.security.BoardAuthorities.CATEGORY_MANAGE;
import static com.seatliberator.seatliberator.board.infrastructure.security.BoardAuthorities.POST_CREATE;
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
                        .with(auth(CATEGORY_MANAGE))
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
                        .with(auth(POST_CREATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postCreateBody(categoryId)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn404WhenCreatingPostWithNonExistingCategoryId() throws Exception {
        mockMvc.perform(post("/board/{boardId}/posts", boardId)
                        .with(auth(POST_CREATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postCreateBody(UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenCreatingPostWithCategoryFromAnotherBoard() throws Exception {
        mockMvc.perform(post("/board/{boardId}/posts", boardId)
                        .with(auth(POST_CREATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postCreateBody(otherBoardCategoryId)))
                .andExpect(status().isNotFound());
    }

    private String postCreateBody(UUID requestCategoryId) {
        return """
                {"categoryId":"%s","title":"title","content":"content"}
                """.formatted(requestCategoryId);
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
