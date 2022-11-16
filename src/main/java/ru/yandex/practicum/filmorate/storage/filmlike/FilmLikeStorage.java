package ru.yandex.practicum.filmorate.storage.filmlike;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;

public interface FilmLikeStorage {
    List<FilmLike> getAll();

    FilmLike createWithFilmIdAndUserId(Film film, User user) throws EntityAlreadyExistsException;

    void deleteByFilmIdAndUserId(Film film, User user) throws EntityIsNotFoundException;

    List<Long> getFilmIdsAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(Integer limit);
}
