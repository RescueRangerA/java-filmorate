package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilmReview {
    @Positive
    @Nullable
    private Long reviewId;

    @NotBlank
    @NotNull
    private String content;

    @NotNull
    private Boolean isPositive;

    @NotNull
    private Long filmId;

    @NotNull
    private Long userId;

    private Integer useful = 0;
}
