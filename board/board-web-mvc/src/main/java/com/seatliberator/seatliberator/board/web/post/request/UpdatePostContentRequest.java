package com.seatliberator.seatliberator.board.web.post.request;

import com.seatliberator.seatliberator.board.application.post.port.in.command.UpdatePostContentCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "게시글 정보 변경 요청")
public record UpdatePostContentRequest(
        @Schema(description = "변경할 게시글 본문", example = "오후 7시부터 9시까지 알고리즘 스터디를 진행합니다.")
        @NotBlank String content
) {
    public UpdatePostContentCommand toCommand(UUID postId) {
        return UpdatePostContentCommand.of(postId, content);
    }
}
