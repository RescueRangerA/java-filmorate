package ru.yandex.practicum.filmorate.storage.filmgenre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

public interface FilmGenreStorage {
    Iterable<FilmGenre> findFilmGenreAll();

    Iterable<FilmGenre> findAllByFilm(Film film);

    FilmGenre saveFilmGenre(FilmGenre entity);

    void deleteFilmGenre(FilmGenre entity);

    void deleteAllByFilm(Film film);
}
