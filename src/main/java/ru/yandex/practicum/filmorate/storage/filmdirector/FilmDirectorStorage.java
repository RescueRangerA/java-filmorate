package ru.yandex.practicum.filmorate.storage.filmdirector;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmDirectorStorage {
    void saveDirectorsOfTheFilm(final Film film);

    void deleteDirectorsFromFilm(final Film film);

}
