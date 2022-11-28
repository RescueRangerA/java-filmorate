package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilmLike {
    @Positive
    private Long filmId;

    @Positive
    private Long usedId;
}
