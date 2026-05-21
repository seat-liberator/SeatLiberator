package com.seatliberator.seatliberator.board.web.category.request;

import com.seatliberator.seatliberator.board.application.category.port.in.command.UpdateCategoryDescriptionCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "카테고리 설명 변경 요청")
public record UpdateCategoryDescriptionRequest(
        @Schema(description = "변경할 카테고리 설명", example = "서비스 이용 질문을 남기는 카테고리입니다.")
        @NotBlank String description
) {
    public UpdateCategoryDescriptionCommand toCommand(UUID categoryId) {
        return UpdateCategoryDescriptionCommand.of(categoryId, description);
    }
}
