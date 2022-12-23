package ru.yandex.practicum.filmorate.storage.filmgenre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenreDirector;

import java.util.List;

public interface FilmGenreStorage {
    void deleteAllGenresOfTheFilm(Film film);

    Film saveGenresOfTheFilm(Film film);

    List<FilmGenreDirector> findFilmGenresOfTheFilms(List<Film> filmEntities);
}
