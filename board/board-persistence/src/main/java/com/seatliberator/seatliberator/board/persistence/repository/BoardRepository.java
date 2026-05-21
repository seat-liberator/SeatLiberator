package com.seatliberator.seatliberator.board.persistence.repository;

import com.seatliberator.seatliberator.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID> {
    @Query("select b from Board b left join fetch b.categories where b.id = :boardId")
    Optional<Board> findByIdWithCategories(@Param("boardId") UUID boardId);
}
