package com.seatliberator.seatliberator.board.web.post.request;

import com.seatliberator.seatliberator.board.application.post.port.in.command.UpdatePostTitleCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "게시글 제목 변경 요청")
public record UpdatePostTitleRequest(
        @Schema(description = "변경할 게시글 제목", example = "오늘 저녁 스터디 같이 하실 분")
        @NotBlank String title
) {
    public UpdatePostTitleCommand toCommand(UUID postId) {
        return UpdatePostTitleCommand.of(postId, title);
    }
}
