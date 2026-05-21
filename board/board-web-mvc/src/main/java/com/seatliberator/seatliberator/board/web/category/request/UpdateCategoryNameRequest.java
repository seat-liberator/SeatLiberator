package com.seatliberator.seatliberator.board.web.category.request;

import com.seatliberator.seatliberator.board.application.category.port.in.command.UpdateCategoryNameCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "카테고리 이름 변경 요청")
public record UpdateCategoryNameRequest(
        @Schema(description = "변경할 카테고리 이름", example = "Q&A")
        @NotBlank String name
) {
    public UpdateCategoryNameCommand toCommand(UUID categoryId) {
        return UpdateCategoryNameCommand.of(categoryId, name);
    }
}
