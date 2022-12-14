package ru.yandex.practicum.filmorate.storage.filmgenre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreStorage {
    void deleteAllGenresOfTheFilm(Film film);

    Film saveGenresOfTheFilm(Film film);

    List<FilmGenre> findFilmGenresOfTheFilms(List<Film> filmEntities);
}
