package ru.yandex.practicum.filmorate.storage.filmlike;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;

public interface FilmLikeStorage {
    Iterable<FilmLike> findAll();

    FilmLike save(FilmLike entity);

    void delete(FilmLike entity);

    Iterable<Film> getFilmIdsAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(Integer limit);
}
