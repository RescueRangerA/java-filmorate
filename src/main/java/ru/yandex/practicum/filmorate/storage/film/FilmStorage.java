package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findFilmsAll();

    Film saveFilm(Film entity);

    Optional<Film> findFilmById(Long aLong);

    List<Film> findFilmsAllById(List<Long> longs);

    void deleteFilmById(Long aLong);

    FilmLike saveFilmLike(FilmLike entity);

    void deleteFilmLike(FilmLike entity);

    List<Film> findTopNMostLikedFilms(Integer limit);

    List<Film> getFilmsFriends(Long userId, Long friendId);

    List<Film> getFilmByDirector(final Long directorId, final String sortBy);
}
