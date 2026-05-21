package com.seatliberator.seatliberator.board.web.board.request;

import com.seatliberator.seatliberator.board.application.board.port.in.command.UpdateBoardNameCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "게시판 정보 변경 요청")
public record UpdateBoardNameRequest(
        @Schema(description = "변경할 게시판 이름", example = "자유게시판")
        @NotBlank String name
) {
    public UpdateBoardNameCommand toCommand(UUID boardId) {
        return UpdateBoardNameCommand.of(boardId, name);
    }
}
