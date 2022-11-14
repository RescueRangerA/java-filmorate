package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class FilmLike implements Entity {
    @Positive
    @Nullable
    private Long id;

    @Positive
    private Long filmId;

    @Positive
    private Long usedId;
}
