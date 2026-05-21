package com.seatliberator.seatliberator.board.web.post.request;

import com.seatliberator.seatliberator.board.application.post.port.in.command.CreatePostCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "게시글 생성 요청")
public record CreatePostRequest(
        @Schema(description = "게시글이 속할 카테고리 ID", example = "00000000-0000-0000-0000-000000000001")
        @NotNull UUID categoryId,
        @Schema(description = "게시글 제목", example = "오늘 오후 스터디 같이 하실 분")
        @NotBlank String title,
        @Schema(description = "게시글 본문", example = "오후 2시부터 4시까지 알고리즘 스터디를 진행합니다.")
        @NotBlank String content
) {
    public CreatePostCommand toCommand(UUID boardId, String userId) {
        return CreatePostCommand.of(boardId, categoryId, userId, title, content);
    }
}
