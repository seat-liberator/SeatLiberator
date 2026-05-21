package com.seatliberator.seatliberator.board.persistence.category;

import com.seatliberator.seatliberator.board.application.category.port.out.CategoryReader;
import com.seatliberator.seatliberator.board.application.category.port.out.CategoryStore;
import com.seatliberator.seatliberator.board.application.category.port.out.criteria.CategoryBoardCriteria;
import com.seatliberator.seatliberator.board.application.category.port.out.filter.CategoryFilter;
import com.seatliberator.seatliberator.board.domain.Category;
import com.seatliberator.seatliberator.board.persistence.category.repository.CategoryRepository;
import com.seatliberator.seatliberator.board.persistence.shared.predicates.CommonPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaCategoryPersistenceAdapter implements CategoryReader, CategoryStore {
    private final CategoryRepository repository;

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Category> findByCriteria(CategoryBoardCriteria criteria) {
        var spec = createSpecificationFromCriteria(criteria);
        return repository.findAll(spec);
    }

    @Override
    public Category save(Category category) {
        return repository.save(category);
    }

    @Override
    public void delete(Category category) {
        repository.delete(category);
    }

    private Specification<Category> createSpecificationFromCriteria(CategoryBoardCriteria criteria) {
        var spec = createSpecificationFromFilter(criteria.filter());

        spec = spec.and(CommonPredicates.equals(criteria.boardId(), from -> from.get("boardId")));

        return spec;
    }

    private Specification<Category> createSpecificationFromFilter(CategoryFilter filter) {
        var spec = Specification.<Category>unrestricted();

        if (filter.name() != null) {
            spec = spec.and(CommonPredicates.like(filter.name(), from -> from.get("name")));
        }

        if (filter.description() != null) {
            spec = spec.and(CommonPredicates.like(filter.description(), from -> from.get("description")));
        }

        return spec;
    }
}
