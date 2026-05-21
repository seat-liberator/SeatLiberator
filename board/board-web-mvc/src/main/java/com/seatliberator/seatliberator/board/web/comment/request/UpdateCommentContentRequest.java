package com.seatliberator.seatliberator.board.web.comment.request;

import com.seatliberator.seatliberator.board.application.comment.port.in.command.UpdateCommentContentCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "댓글 본문 변경 요청")
public record UpdateCommentContentRequest(
        @Schema(description = "댓글 본문", example = "안녕하세요")
        @NotBlank String content
) {
    public UpdateCommentContentCommand toCommand(UUID commentId) {
        return UpdateCommentContentCommand.of(commentId, content);
    }
}
