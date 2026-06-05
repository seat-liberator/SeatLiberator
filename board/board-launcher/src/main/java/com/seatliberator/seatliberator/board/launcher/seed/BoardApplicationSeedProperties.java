package com.seatliberator.seatliberator.board.launcher.seed;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "seatliberator.board.launcher.seed")
public record BoardApplicationSeedProperties(
        @Valid
        @NonNull
        @DefaultValue
        Board board,

        @Valid
        @NonNull
        @DefaultValue
        Category category,

        @Valid
        @NonNull
        @DefaultValue
        Post post
) {
    public record Board(
            @NotBlank
            @DefaultValue("Test Board #%s")
            String namePrefixFormat,

            @NotBlank
            @DefaultValue("Test Board Description #%s")
            String descriptionPrefixFormat,


            @PositiveOrZero
            @DefaultValue("10")
            int num
    ) {
    }

    public record Category(
            @NotBlank
            @DefaultValue("Test Category #%s")
            String namePrefixFormat,

            @NotBlank
            @DefaultValue("Test Category Description #%s")
            String descriptionPrefixFormat,

            @PositiveOrZero
            @DefaultValue("3")
            int num
    ) {
    }

    public record Post(
            @NotBlank
            @DefaultValue("Test Post #%s")
            String titlePrefixFormat,

            @NotBlank
            @DefaultValue("Test Post Content #%s")
            String contentPrefixFormat,

            @PositiveOrZero
            @DefaultValue("10")
            int num
    ) {
    }
}
