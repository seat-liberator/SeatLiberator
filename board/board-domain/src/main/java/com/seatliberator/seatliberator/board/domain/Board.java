package com.seatliberator.seatliberator.board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    @OneToMany(
            mappedBy = "board",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Post> posts = new ArrayList<>();
    @OneToMany(
            mappedBy = "board",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Category> categories = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Setter
    @Column(nullable = false)
    private String name;
    @Setter
    @Column
    private String description;

    private Board(
            String name,
            String description
    ) {
        this.name = name;
        this.description = description;
    }

    public static Board create(
            String name,
            String description
    ) {
        return new Board(name, description);
    }

    public Post addPost(
            String title,
            String content,
            Category category
    ) {
        var post = Post.create(title, content);
        this.posts.add(post);
        post.setBoard(this);
        post.setCategory(category);
        return post;
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        post.setBoard(null);
    }

    public void changePostCategory(Post post, Category category) {
        post.setCategory(category);
    }

    public Optional<Post> findPost(UUID postId) {
        return this.posts.stream()
                .filter(post -> post.getId().equals(postId))
                .findFirst();
    }

    public Category addCategory(String name, String description) {
        var category = Category.create(name, description);
        this.categories.add(category);
        category.setBoard(this);
        return category;
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.setBoard(null);
    }

    public Optional<Category> findCategory(UUID categoryId) {
        return this.categories.stream()
                .filter(category -> category.getId().equals(categoryId))
                .findFirst();
    }
}
