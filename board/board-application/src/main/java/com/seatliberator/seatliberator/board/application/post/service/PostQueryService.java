package com.seatliberator.seatliberator.board.application.post.service;

import com.seatliberator.seatliberator.board.application.post.port.in.FindPostUseCase;
import com.seatliberator.seatliberator.board.application.post.port.in.ListCategoryPostUseCase;
import com.seatliberator.seatliberator.board.application.post.port.in.ListUserPostUseCase;
import com.seatliberator.seatliberator.board.application.post.port.in.query.FindPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.query.ListCategoryPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.query.ListUserPostQuery;
import com.seatliberator.seatliberator.board.application.post.port.in.result.PostResult;
import com.seatliberator.seatliberator.board.application.post.port.out.PostReader;
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
        ListCategoryPostUseCase,
        ListUserPostUseCase {

    private final PostReader reader;

    @Override
    public PostResult find(FindPostQuery query) {
        var postId = query.postId();
        var post = reader.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return PostResult.from(post);
    }

    @Override
    public List<PostResult> list(ListCategoryPostQuery query) {
        return reader.findByFilter(query.toFilter()).stream()
                .map(PostResult::from)
                .toList();
    }

    @Override
    public List<PostResult> list(ListUserPostQuery query) {
        return reader.findByFilter(query.toFilter()).stream()
                .map(PostResult::from)
                .toList();
    }
}
