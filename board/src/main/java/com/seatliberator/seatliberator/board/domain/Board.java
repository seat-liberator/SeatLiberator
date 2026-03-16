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
            String content
    ) {
        var post = Post.create(title, content);
        this.posts.add(post);
        post.setBoard(this);
        return post;
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        post.setBoard(null);
    }

    public Optional<Post> findPost(UUID postId) {
        return this.posts.stream()
                .filter(post -> post.getId().equals(postId))
                .findFirst();
    }
}
