package ru.yandex.practicum.filmorate.storage.filmlike;

import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;

public interface FilmLikeStorage {
    List<FilmLike> getAll();

    FilmLike createWithFilmIdAndUserId(Long filmId, Long usedId) throws EntityAlreadyExistsException;

    void deleteByFilmIdAndUserId(Long filmId, Long usedId) throws EntityIsNotFoundException;

    List<List<FilmLike>> getAllAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(Integer limit);
}
