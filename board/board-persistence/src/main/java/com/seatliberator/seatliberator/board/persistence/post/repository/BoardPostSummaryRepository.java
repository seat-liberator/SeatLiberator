package com.seatliberator.seatliberator.board.persistence.post.repository;

import com.seatliberator.seatliberator.board.domain.Post;
import com.seatliberator.seatliberator.board.persistence.board.row.BoardPostSummaryRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BoardPostSummaryRepository extends JpaRepository<Post, UUID> {
    @Query("""
            SELECT
                post.boardId AS boardId,
                post.id AS postId,
                category.id AS categoryId,
                category.name AS categoryName,
                post.userId AS userId,
                post.title AS title,
                post.createdAt AS createdAt,
                post.updatedAt AS updatedAt,
                (
                    SELECT COUNT(comment.id)
                    FROM Comment comment
                    WHERE comment.postId = post.id
                ) AS commentCount
            FROM Post post
            LEFT JOIN Category category
                ON category.id = post.categoryId
            WHERE post.boardId = :boardId
                AND (:categoryId IS NULL OR post.categoryId = :categoryId)
            ORDER BY post.createdAt DESC, post.updatedAt DESC, post.id ASC
            """)
    List<BoardPostSummaryRow> findRowsByBoardId(
            @Param("boardId") UUID boardId,
            @Param("categoryId") UUID categoryId
    );
}
