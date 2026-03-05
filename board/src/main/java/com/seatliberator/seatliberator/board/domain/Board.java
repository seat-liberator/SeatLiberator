package com.seatliberator.seatliberator.board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = true)
    private String description;

    @OneToMany(
            mappedBy = "board",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Post> posts = new ArrayList<>();

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

    public void addPost(
            String title,
            String content
    ) {
        var post = Post.create(title, content);
        this.posts.add(post);
        post.setBoard(this);
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        post.setBoard(null);
    }
}
