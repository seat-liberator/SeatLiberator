package com.seatliberator.seatliberator.board.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "게시글 생성 요청")
public record PostCreateRequest(
        @Schema(description = "게시글이 속할 카테고리 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e4")
        @NotNull(message = "Category id is required.")
        UUID categoryId,
        @Schema(description = "게시글 제목", example = "오늘 오후 스터디 같이 하실 분")
        @NotBlank(message = "Post title is required.")
        String title,
        @Schema(description = "게시글 본문", example = "오후 2시부터 4시까지 알고리즘 스터디를 진행합니다.")
        @NotBlank(message = "Post content is required.")
        String content
) {
}
