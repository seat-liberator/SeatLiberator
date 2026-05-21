package com.seatliberator.seatliberator.board.web.comment.request;

import com.seatliberator.seatliberator.board.application.comment.port.in.command.CreateCommentCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "댓글 생성 요청")
public record CreateCommentRequest(
        @Schema(description = "댓글 본문", example = "안녕하세요")
        @NotBlank String content
) {
    public CreateCommentCommand toCommand(UUID postId, String userId) {
        return CreateCommentCommand.of(postId, userId, content);
    }
}
