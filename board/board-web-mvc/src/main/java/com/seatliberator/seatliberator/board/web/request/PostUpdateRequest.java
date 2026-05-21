package com.seatliberator.seatliberator.board.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Schema(description = "게시글 정보 변경 요청")
public record PostUpdateRequest(
        @Schema(description = "변경할 카테고리 ID", example = "018f2d5d-6a8d-7b42-9c1a-0c7a08b1d2e4")
        @Nullable UUID categoryId,
        @Schema(description = "변경할 게시글 제목", example = "오늘 저녁 스터디 같이 하실 분")
        @Nullable String title,
        @Schema(description = "변경할 게시글 본문", example = "오후 7시부터 9시까지 알고리즘 스터디를 진행합니다.")
        @Nullable String content
) {
}
