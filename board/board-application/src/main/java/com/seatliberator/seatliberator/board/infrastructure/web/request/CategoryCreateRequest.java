package com.seatliberator.seatliberator.board.infrastructure.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카테고리 생성 요청")
public record CategoryCreateRequest(
        @Schema(description = "카테고리 이름", example = "스터디 모집")
        @NotBlank(message = "Category name is required.")
        String name,
        @Schema(description = "카테고리 설명", example = "스터디 모집 글을 모아보는 카테고리입니다.")
        String description
) {
}
