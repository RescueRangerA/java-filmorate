package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Optional;

public interface GenreStorage {
    Iterable<Genre> findAll();

    Optional<Genre> findById(Long aLong);

    Iterable<Genre> findAllById(Iterable<Long> longs);
}
