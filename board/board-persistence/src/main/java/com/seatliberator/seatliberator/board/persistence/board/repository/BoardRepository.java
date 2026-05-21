package com.seatliberator.seatliberator.board.persistence.board.repository;

import com.seatliberator.seatliberator.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID>, JpaSpecificationExecutor<Board> {
}
