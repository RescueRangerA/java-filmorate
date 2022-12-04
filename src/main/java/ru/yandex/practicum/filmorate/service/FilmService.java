package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    final private FilmStorage filmStorage;

    final private FilmLikeStorage filmLikeStorage;

    final private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, FilmLikeStorage filmLikeStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return (List<Film>) filmStorage.findAll();
    }

    public Film save(Film film) {
        return filmStorage.save(film);
    }

    public Film getById(Long filmId) {
        return filmStorage.findById(filmId).orElseThrow(() -> new EntityIsNotFoundException(Film.class, 0L));
    }

    public FilmLike addLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);

        if ( film.isEmpty() ) {
            throw new EntityIsNotFoundException(Film.class, 0L);
        }

        if ( user.isEmpty() ) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        return filmLikeStorage.save(new FilmLike(film.get(), user.get()));
    }

    public void removeLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);

        if ( film.isEmpty() ) {
            throw new EntityIsNotFoundException(Film.class, 0L);
        }

        if ( user.isEmpty() ) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        filmLikeStorage.delete(new FilmLike(film.get(), user.get()));
    }

    public List<Film> getPopularFilms(Integer limit) {
        return (List<Film>) filmLikeStorage.getFilmIdsAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(limit);
    }
}
