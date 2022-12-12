package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    final private FilmStorage filmStorage;

    final private UserStorage userStorage;

    final private GenreStorage genreStorage;

    @Autowired
    public FilmService(
            FilmStorage filmStorage,
            UserStorage userStorage,
            GenreStorage genreStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
    }

    public List<Film> findAll() {
        return (List<Film>) filmStorage.findFilmsAll();
    }

    public Film save(Film film) {
        Film newFilm = filmStorage.saveFilm(film);

        return getById(newFilm.getId());
    }

    public Film getById(Long filmId) {
        return filmStorage.findFilmById(filmId).orElseThrow(() -> new EntityIsNotFoundException(Film.class, filmId));
    }

    public FilmLike addLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findFilmById(filmId);
        Optional<User> user = userStorage.findById(userId);

        if (film.isEmpty()) {
            throw new EntityIsNotFoundException(Film.class, filmId);
        }

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userId);
        }

        return filmStorage.saveFilmLike(new FilmLike(film.get(), user.get()));
    }

    public void removeLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findFilmById(filmId);
        Optional<User> user = userStorage.findById(userId);

        if (film.isEmpty()) {
            throw new EntityIsNotFoundException(Film.class, filmId);
        }

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userId);
        }

        filmStorage.deleteFilmLike(new FilmLike(film.get(), user.get()));
    }

    public List<Film> getPopularFilms(Integer limit) {
        return (List<Film>) filmStorage.findTopNMostLikedFilms(limit);
    }

    public Genre findGenreById(Long aLong) {
        return genreStorage.findById(aLong).orElseThrow(() -> new EntityIsNotFoundException(Genre.class, aLong));
    }

    public Iterable<Genre> findAllGenres() {
        return genreStorage.findAll();
    }

    public FilmMpaRating findRatingById(Long aLong) {
        return filmStorage.findMpaRatingById(aLong).orElseThrow(() -> new EntityIsNotFoundException(FilmMpaRating.class, aLong));
    }

    public Iterable<FilmMpaRating> findAllRatings() {
        return filmStorage.findMpaRatingsAll();
    }
}
