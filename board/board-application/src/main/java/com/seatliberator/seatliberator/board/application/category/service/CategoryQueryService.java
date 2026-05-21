package com.seatliberator.seatliberator.board.application.category.service;

import com.seatliberator.seatliberator.board.application.board.port.out.BoardReader;
import com.seatliberator.seatliberator.board.application.category.port.in.FindCategoryUseCase;
import com.seatliberator.seatliberator.board.application.category.port.in.ListCategoryUseCase;
import com.seatliberator.seatliberator.board.application.category.port.in.query.FindCategoryQuery;
import com.seatliberator.seatliberator.board.application.category.port.in.query.ListCategoryQuery;
import com.seatliberator.seatliberator.board.application.category.port.in.result.CategoryResult;
import com.seatliberator.seatliberator.board.application.category.port.out.CategoryReader;
import com.seatliberator.seatliberator.board.application.category.port.out.criteria.CategoryBoardCriteria;
import com.seatliberator.seatliberator.board.application.shared.exception.BoardNotFoundException;
import com.seatliberator.seatliberator.board.application.shared.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService implements
        FindCategoryUseCase,
        ListCategoryUseCase {

    private final CategoryReader reader;
    private final BoardReader boardReader;

    @Override
    public CategoryResult find(FindCategoryQuery query) {
        var categoryId = query.categoryId();
        return reader.findById(categoryId)
                .map(CategoryResult::from)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    @Override
    public List<CategoryResult> list(ListCategoryQuery query) {
        var boardId = query.boardId();
        var existsBoard = boardReader.existsById(boardId);
        if (!existsBoard) throw new BoardNotFoundException(boardId);

        var criteria = CategoryBoardCriteria.of(boardId, query.toFilter());
        return reader.findByCriteria(criteria).stream()
                .map(CategoryResult::from)
                .toList();
    }
}
