package com.seatliberator.seatliberator.board.web.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시판 생성 요청")
public record BoardCreateRequest(
        @Schema(description = "게시판 이름", example = "공지사항")
        String name,
        @Schema(description = "게시판 설명", example = "서비스 공지와 운영 안내를 게시하는 공간입니다.")
        String description
) {
}
