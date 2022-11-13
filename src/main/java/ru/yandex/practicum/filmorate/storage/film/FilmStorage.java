package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.UpdateNonExistingEntity;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    Film create(Film filmEntity);

    Film update(Film filmEntity) throws UpdateNonExistingEntity;
}
