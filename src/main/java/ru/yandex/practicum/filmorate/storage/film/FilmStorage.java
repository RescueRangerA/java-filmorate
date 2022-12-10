package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;

import java.util.Optional;

public interface FilmStorage {
    Iterable<Film> findFilmsAll();

    Film saveFilm(Film entity);

    Optional<Film> findFilmById(Long aLong);

    Iterable<Film> findFilmsAllById(Iterable<Long> longs);

    void deleteFilmById(Long aLong);

    void deleteFilm(Film entity);

    Iterable<FilmMpaRating> findMpaRatingsAll();

    Optional<FilmMpaRating> findMpaRatingById(Long aLong);

    FilmLike saveFilmLike(FilmLike entity);

    void deleteFilmLike(FilmLike entity);

    Iterable<Film> findTopNMostLikedFilms(Integer limit);
}
