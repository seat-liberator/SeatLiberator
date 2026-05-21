package com.seatliberator.seatliberator.board.persistence.post;

import com.seatliberator.seatliberator.board.application.post.port.in.result.BoardPostSummaryResult;
import com.seatliberator.seatliberator.board.application.post.port.out.BoardPostSummaryReader;
import com.seatliberator.seatliberator.board.application.post.port.out.filter.PostFilter;
import com.seatliberator.seatliberator.board.persistence.board.row.BoardPostSummaryRow;
import com.seatliberator.seatliberator.board.persistence.post.repository.BoardPostSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaBoardPostSummaryPersistenceAdapter implements BoardPostSummaryReader {
    private final BoardPostSummaryRepository repository;

    @Override
    public List<BoardPostSummaryResult> findByFilter(PostFilter filter) {
        return repository.findRowsByBoardId(filter.boardId(), filter.categoryId()).stream()
                .map(BoardPostSummaryRow::toResult)
                .toList();
    }
}
