package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilmLike {
    @Positive
    @Nullable
    private Long id;

    @Positive
    private Long filmId;

    @Positive
    private Long usedId;
}
