package ru.yandex.practicum.filmorate.storage.mparating;

import ru.yandex.practicum.filmorate.model.FilmMpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaRatingStorage {
    List<FilmMpaRating> findAll();

    Optional<FilmMpaRating> findById(Long aLong);
}
