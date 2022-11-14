package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    List<Film> getAll();

    Set<Film> getFirstN(Integer limit);

    Film get(Long filmId) throws EntityIsNotFoundException;

    Set<Film> getMany(Set<Long> filmIds);

    Film create(Film filmEntity);

    Film update(Film filmEntity) throws EntityIsNotFoundException;
}
