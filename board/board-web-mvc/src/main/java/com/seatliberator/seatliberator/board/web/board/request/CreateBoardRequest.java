package com.seatliberator.seatliberator.board.web.board.request;

import com.seatliberator.seatliberator.board.application.board.port.in.command.CreateBoardCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "게시판 생성 요청")
public record CreateBoardRequest(
        @Schema(description = "게시판 이름", example = "공지사항")
        @NotBlank String name,
        @Schema(description = "게시판 설명", example = "서비스 공지와 운영 안내를 게시하는 공간입니다.")
        @NotBlank String description
) {
    public CreateBoardCommand toCommand() {
        return new CreateBoardCommand(name, description);
    }
}
