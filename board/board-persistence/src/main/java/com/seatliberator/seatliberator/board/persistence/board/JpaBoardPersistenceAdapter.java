package com.seatliberator.seatliberator.board.persistence.board;

import com.seatliberator.seatliberator.board.application.board.port.out.BoardReader;
import com.seatliberator.seatliberator.board.application.board.port.out.BoardStore;
import com.seatliberator.seatliberator.board.application.board.port.out.filter.BoardFilter;
import com.seatliberator.seatliberator.board.domain.Board;
import com.seatliberator.seatliberator.board.persistence.board.repository.BoardRepository;
import com.seatliberator.seatliberator.board.persistence.shared.predicates.CommonPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaBoardPersistenceAdapter implements BoardReader, BoardStore {
    private final BoardRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<Board> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Board> findByFilter(BoardFilter filter) {
        var spec = createSpecificationFromFilter(filter);
        return repository.findAll(spec);
    }

    @Override
    public Board save(Board board) {
        return repository.save(board);
    }

    @Override
    public void delete(Board board) {
        repository.delete(board);
    }

    private Specification<Board> createSpecificationFromFilter(BoardFilter filter) {
        var spec = Specification.<Board>unrestricted();

        if (filter.name() != null) {
            spec = spec.and(CommonPredicates.like(filter.name(), from -> from.get("name")));
        }

        if (filter.description() != null) {
            spec = spec.and(CommonPredicates.like(filter.description(), from -> from.get("description")));
        }

        return spec;
    }
}
