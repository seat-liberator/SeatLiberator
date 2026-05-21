package com.seatliberator.seatliberator.board.application.post.port.out;

import com.seatliberator.seatliberator.board.application.post.port.in.result.BoardPostSummaryResult;
import com.seatliberator.seatliberator.board.application.post.port.out.filter.PostFilter;

import java.util.List;

public interface BoardPostSummaryReader {
    List<BoardPostSummaryResult> findByFilter(PostFilter filter);
}
