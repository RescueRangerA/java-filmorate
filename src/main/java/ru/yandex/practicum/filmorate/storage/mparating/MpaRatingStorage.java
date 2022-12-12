package ru.yandex.practicum.filmorate.storage.mparating;

import ru.yandex.practicum.filmorate.model.FilmMpaRating;

import java.util.Optional;

public interface MpaRatingStorage {
    Iterable<FilmMpaRating> findAll();

    Optional<FilmMpaRating> findById(Long aLong);
}
