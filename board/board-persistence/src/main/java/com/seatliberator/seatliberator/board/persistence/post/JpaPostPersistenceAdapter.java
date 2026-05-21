package com.seatliberator.seatliberator.board.persistence.post;

import com.seatliberator.seatliberator.board.application.post.port.out.PostReader;
import com.seatliberator.seatliberator.board.application.post.port.out.PostStore;
import com.seatliberator.seatliberator.board.application.post.port.out.filter.PostFilter;
import com.seatliberator.seatliberator.board.domain.Post;
import com.seatliberator.seatliberator.board.persistence.post.repository.PostRepository;
import com.seatliberator.seatliberator.board.persistence.shared.predicates.CommonPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaPostPersistenceAdapter implements PostReader, PostStore {
    private final PostRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<Post> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Post> findByFilter(PostFilter filter) {
        var spec = createSpecificationFromFilter(filter);
        return repository.findAll(spec);
    }

    @Override
    public Post save(Post post) {
        return repository.save(post);
    }

    @Override
    public void delete(Post post) {
        repository.delete(post);
    }

    private Specification<Post> createSpecificationFromFilter(PostFilter filter) {
        var spec = Specification.<Post>unrestricted();

        if (filter.boardId() != null) {
            spec = spec.and(CommonPredicates.equals(filter.boardId(), from -> from.get("boardId")));
        }

        if (filter.categoryId() != null) {
            spec = spec.and(CommonPredicates.equals(filter.categoryId(), from -> from.get("categoryId")));
        }

        if (filter.userId() != null) {
            spec = spec.and(CommonPredicates.equals(filter.userId(), from -> from.get("userId")));
        }

        return spec;
    }
}
