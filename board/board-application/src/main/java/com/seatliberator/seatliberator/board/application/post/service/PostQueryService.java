package com.seatliberator.seatliberator.board.application.post.service;

import com.seatliberator.seatliberator.board.application.board.port.out.BoardReader;
import com.seatliberator.seatliberator.board.application.post.port.in.FindPostUseCase;
import com.seatliberator.seatliberator.board.application.post.port.in.ListPostUseCase;
import com.seatliberator.seatliberator.board.application.post.port.in.query.FindPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.query.ListPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;
import com.seatliberator.seatliberator.board.application.post.port.out.PostReader;
import com.seatliberator.seatliberator.board.application.shared.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.application.shared.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService implements
        FindPostUseCase,
        ListPostUseCase {

    private final PostReader reader;
    private final BoardReader boardReader;

    @Override
    public PostResult find(FindPostQuery query) {
        var postId = query.postId();
        var post = reader.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return PostResult.from(post);
    }

    @Override
    public List<PostResult> list(ListPostQuery query) {
        var boardId = query.boardId();
        var existsBoard = boardReader.existsById(boardId);
        if (!existsBoard) throw new BoardNotFoundException(boardId);

        return reader.findByFilter(query.toFilter()).stream()
                .map(PostResult::from)
                .toList();
    }
}
