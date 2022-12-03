package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;

public interface FilmStorage {
    Iterable<Film> findAll();

    Film save(Film entity);

    Optional<Film> findById(Long aLong);

    Iterable<Film> findAllById(Iterable<Long> longs);

    void deleteById(Long aLong);

    void delete(Film entity);

    Iterable<Film> findFirstN(Integer limit);
}
