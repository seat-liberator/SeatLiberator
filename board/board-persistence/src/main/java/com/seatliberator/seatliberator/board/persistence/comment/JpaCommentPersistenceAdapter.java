package com.seatliberator.seatliberator.board.persistence.comment;

import com.seatliberator.seatliberator.board.application.comment.port.out.CommentReader;
import com.seatliberator.seatliberator.board.application.comment.port.out.CommentStore;
import com.seatliberator.seatliberator.board.application.comment.port.out.filter.CommentFilter;
import com.seatliberator.seatliberator.board.domain.Comment;
import com.seatliberator.seatliberator.board.persistence.comment.repository.CommentRepository;
import com.seatliberator.seatliberator.board.persistence.shared.predicates.CommonPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaCommentPersistenceAdapter implements CommentReader, CommentStore {
    private final CommentRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<Comment> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Comment> findByFilter(CommentFilter filter) {
        var spec = createSpecificationFromFilter(filter);
        return repository.findAll(spec);
    }

    @Override
    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        repository.delete(comment);
    }

    private Specification<Comment> createSpecificationFromFilter(CommentFilter filter) {
        var spec = Specification.<Comment>unrestricted();

        if (filter.postId() != null) {
            spec = spec.and(CommonPredicates.equals(filter.postId(), from -> from.get("postId")));
        }

        if (filter.userId() != null) {
            spec = spec.and(CommonPredicates.equals(filter.userId(), from -> from.get("userId")));
        }

        return spec;
    }
}
