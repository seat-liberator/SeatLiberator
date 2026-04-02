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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
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
    @Setter(AccessLevel.PROTECTED)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    private Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Category create(String name, String description) {
        return new Category(name, description);
    }
}
