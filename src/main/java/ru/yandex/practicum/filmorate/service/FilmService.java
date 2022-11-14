package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private FilmStorage filmStorage;

    private FilmLikeStorage filmLikeStorage;

    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, FilmLikeStorage filmLikeStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) throws EntityIsNotFoundException {
        return filmStorage.update(film);
    }

    public Film getById(Long filmId) throws EntityIsNotFoundException {
        return filmStorage.get(filmId);
    }

    public FilmLike addLike(Long filmId, Long userId) throws EntityAlreadyExistsException, EntityIsNotFoundException {
        filmStorage.get(filmId);
        userStorage.getById(userId);

        return filmLikeStorage.createWithFilmIdAndUserId(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) throws EntityIsNotFoundException {
        filmStorage.get(filmId);
        userStorage.getById(userId);

        filmLikeStorage.deleteByFilmIdAndUserId(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer limit) {
        List<Film> popularFilms = filmStorage.getMany(
                filmLikeStorage.getFilmIdsAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(limit)
        );

        if (popularFilms.size() < limit) {
            popularFilms.addAll(filmStorage.getFirstN(limit - popularFilms.size()));
        }

        return popularFilms;
    }
}
