package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FilmService {
    final private FilmStorage filmStorage;

    final private FilmLikeStorage filmLikeStorage;

    final private UserStorage userStorage;

    final private GenreStorage genreStorage;

    final private FilmGenreStorage filmGenreStorage;

    final private MpaRatingStorage mpaRatingStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("filmLikeDbStorage") FilmLikeStorage filmLikeStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            GenreStorage genreStorage,
            FilmGenreStorage filmGenreStorage,
            MpaRatingStorage mpaRatingStorage
    ) {
        this.filmStorage = filmStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public List<Film> findAll() {
        List<Film> listOfFilms = new ArrayList<>();

        for (Film film : filmStorage.findAll()) {
            film.setGenres(
                    StreamSupport
                            .stream(filmGenreStorage.findAllByFilm(film).spliterator(), false)
                            .map(FilmGenre::getGenre)
                            .collect(Collectors.toSet())
            );

            listOfFilms.add(film);
        }

        return listOfFilms;
    }

    public Film save(Film film) {
        Film newFilm = filmStorage.save(film);
        filmGenreStorage.deleteAllByFilm(newFilm);
        newFilm.getGenres().forEach((genre -> filmGenreStorage.saveFilmGenre(new FilmGenre(newFilm, genre))));

        return getById(film.getId());
    }

    public Film getById(Long filmId) {
        Film newFilm = filmStorage.findById(filmId).orElseThrow(() -> new EntityIsNotFoundException(Film.class, filmId));
        StreamSupport
                .stream(filmGenreStorage.findAllByFilm(newFilm).spliterator(), false)
                .map((FilmGenre::getGenre))
                .forEach(newFilm::addGenre);

        return newFilm;
    }

    public FilmLike addLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);

        if (film.isEmpty()) {
            throw new EntityIsNotFoundException(Film.class, 0L);
        }

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        return filmLikeStorage.saveFilmLike(new FilmLike(film.get(), user.get()));
    }

    public void removeLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);

        if (film.isEmpty()) {
            throw new EntityIsNotFoundException(Film.class, 0L);
        }

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        filmLikeStorage.deleteFilmLike(new FilmLike(film.get(), user.get()));
    }

    public List<Film> getPopularFilms(Integer limit) {
        return (List<Film>) filmLikeStorage.findTop10MostLikedFilms(limit);
    }

    public Genre findGenreById(Long aLong) {
        return genreStorage.findById(aLong).orElseThrow(() -> new EntityIsNotFoundException(Genre.class, aLong));
    }

    public Iterable<Genre> findAllGenres() {
        return genreStorage.findAll();
    }

    public FilmMpaRating findRatingById(Long aLong) {
        return mpaRatingStorage.findById(aLong).orElseThrow(() -> new EntityIsNotFoundException(FilmMpaRating.class, aLong));
    }

    public Iterable<FilmMpaRating> findAllRatings() {
        return mpaRatingStorage.findAll();
    }
}
