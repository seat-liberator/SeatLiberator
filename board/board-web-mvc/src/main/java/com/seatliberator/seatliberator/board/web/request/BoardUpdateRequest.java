package com.seatliberator.seatliberator.board.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Schema(description = "게시판 정보 변경 요청")
public record BoardUpdateRequest(
        @Schema(description = "게시판 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e3")
        UUID boardId,
        @Schema(description = "변경할 게시판 이름", example = "자유게시판")
        @Nullable String name,
        @Schema(description = "변경할 게시판 설명", example = "자유롭게 의견을 나누는 공간입니다.")
        @Nullable String description
) {
}
