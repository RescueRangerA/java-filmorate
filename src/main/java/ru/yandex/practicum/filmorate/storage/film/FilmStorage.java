package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    List<Film> getFirstN(Integer limit);

    Film get(Long filmId) throws EntityIsNotFoundException;

    List<Film> getMany(List<Long> filmIds);

    Film create(Film filmEntity);

    Film update(Film filmEntity) throws EntityIsNotFoundException;
}
