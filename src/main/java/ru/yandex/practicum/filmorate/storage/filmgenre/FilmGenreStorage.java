package ru.yandex.practicum.filmorate.storage.filmgenre;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmGenreStorage {
    void deleteAllGenresOfTheFilm(Film film);

    Film saveGenresOfTheFilm(Film film);
}
