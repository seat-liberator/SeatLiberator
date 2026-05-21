package com.seatliberator.seatliberator.board.web.board.request;

import com.seatliberator.seatliberator.board.application.board.port.in.command.UpdateBoardDescriptionCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "게시판 정보 변경 요청")
public record UpdateBoardDescriptionRequest(
        @Schema(description = "변경할 게시판 설명", example = "자유롭게 의견을 나누는 공간입니다.")
        @NotBlank String description
) {
    public UpdateBoardDescriptionCommand toCommand(UUID boardId) {
        return UpdateBoardDescriptionCommand.of(boardId, description);
    }
}
