package com.seatliberator.seatliberator.board.web.category.request;

import com.seatliberator.seatliberator.board.application.category.port.in.command.CreateCategoryCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "카테고리 생성 요청")
public record CreateCategoryRequest(
        @Schema(description = "카테고리 이름", example = "스터디 모집")
        @NotBlank String name,
        @Schema(description = "카테고리 설명", example = "스터디 모집 글을 모아보는 카테고리입니다.")
        @NotBlank String description
) {
    public CreateCategoryCommand toCommand(UUID boardId) {
        return CreateCategoryCommand.of(boardId, name, description);
    }
}
