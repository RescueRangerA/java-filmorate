package ru.yandex.practicum.filmorate.storage.filmlike;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;

public interface FilmLikeStorage {
    Iterable<FilmLike> findFilmLikesAll();

    FilmLike saveFilmLike(FilmLike entity);

    void deleteFilmLike(FilmLike entity);

    Iterable<Film> findTop10MostLikedFilms(Integer limit);
}
