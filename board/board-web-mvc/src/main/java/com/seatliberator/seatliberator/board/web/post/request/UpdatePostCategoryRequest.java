package com.seatliberator.seatliberator.board.web.post.request;

import com.seatliberator.seatliberator.board.application.post.port.in.command.UpdatePostCategoryCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "게시글 정보 변경 요청")
public record UpdatePostCategoryRequest(
        @Schema(description = "변경할 카테고리 ID", example = "00000000-0000-0000-0000-000000000001")
        @NotNull UUID categoryId
) {
    public UpdatePostCategoryCommand toCommand(UUID postId) {
        return UpdatePostCategoryCommand.of(postId, categoryId);
    }

}
