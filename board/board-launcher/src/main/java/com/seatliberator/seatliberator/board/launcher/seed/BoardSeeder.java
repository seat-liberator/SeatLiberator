package com.seatliberator.seatliberator.board.launcher.seed;

import com.seatliberator.seatliberator.board.application.board.port.in.CreateBoardUseCase;
import com.seatliberator.seatliberator.board.application.board.port.in.command.CreateBoardCommand;
import com.seatliberator.seatliberator.board.application.category.port.in.CreateCategoryUseCase;
import com.seatliberator.seatliberator.board.application.category.port.in.command.CreateCategoryCommand;
import com.seatliberator.seatliberator.board.application.post.port.in.CreatePostUseCase;
import com.seatliberator.seatliberator.board.application.post.port.in.command.CreatePostCommand;
import com.seatliberator.seatliberator.starter.launcher.seed.Seeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardSeeder implements Seeder {
    private final CreateBoardUseCase boardUseCase;
    private final CreateCategoryUseCase categoryUseCase;
    private final CreatePostUseCase postUseCase;

    private final BoardApplicationSeedProperties properties;

    @Override
    public void seed() {
        var boardProperties = properties.board();
        var categoryProperties = properties.category();
        var postProperties = properties.post();

        for (int boardNum = 1; boardNum <= boardProperties.num(); boardNum++) {
            var boardName = String.format(boardProperties.namePrefixFormat(), boardNum);
            var boardDescription = String.format(boardProperties.descriptionPrefixFormat(), boardNum);
            var boardCommand = CreateBoardCommand.of(boardName, boardDescription);
            var board = boardUseCase.create(boardCommand);
            log.info("Board created. id=%s".formatted(board.boardId()));

            for (int categoryNum = 1; categoryNum <= categoryProperties.num(); categoryNum++) {
                var categoryName = String.format(categoryProperties.namePrefixFormat(), categoryNum);
                var categoryDescription = String.format(categoryProperties.descriptionPrefixFormat(), categoryNum);
                var categoryCommand = CreateCategoryCommand.of(board.boardId(), categoryName, categoryDescription);
                var category = categoryUseCase.create(categoryCommand);

                for (int postNum = 1; postNum <= postProperties.num(); postNum++) {
                    var postTitle = String.format(postProperties.titlePrefixFormat(), postNum);
                    var postContent = String.format(postProperties.contentPrefixFormat(), postNum);
                    var postCommand = CreatePostCommand.of(board.boardId(), category.categoryId(), new UUID(0, 1L).toString(), postTitle, postContent);
                    var post = postUseCase.create(postCommand);
                }
            }
        }
    }
}
