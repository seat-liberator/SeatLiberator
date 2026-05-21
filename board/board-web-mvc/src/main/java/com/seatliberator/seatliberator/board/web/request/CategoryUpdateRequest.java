package com.seatliberator.seatliberator.board.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "카테고리 정보 변경 요청")
public record CategoryUpdateRequest(
        @Schema(description = "변경할 카테고리 이름", example = "Q&A")
        @Nullable String name,
        @Schema(description = "변경할 카테고리 설명", example = "서비스 이용 질문을 남기는 카테고리입니다.")
        @Nullable String description
) {
}
