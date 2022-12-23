package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {
    final private FilmStorage filmStorage;
    final private MpaRatingStorage mpaRatingStorage;

    final private FilmGenreStorage filmGenreStorage;

    final private UserStorage userStorage;

    final private GenreStorage genreStorage;

    final private FilmDirectorStorage filmDirectorStorage;

    final private DirectorService directorService;

    @Autowired
    public FilmService(
            FilmStorage filmStorage,
            MpaRatingStorage mpaRatingStorage,
            FilmGenreStorage filmGenreStorage,
            UserStorage userStorage,
            GenreStorage genreStorage,
            FilmDirectorStorage filmDirectorStorage,
            DirectorService directorService
    ) {
        this.filmStorage = filmStorage;
        this.mpaRatingStorage = mpaRatingStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.filmDirectorStorage = filmDirectorStorage;
        this.directorService = directorService;
    }

    public List<Film> findAll() {
        return filmStorage.findFilmsAll();
    }

    public Film create(Film film) {
        Film newFilm = filmStorage.saveFilm(film);
        filmGenreStorage.deleteAllGenresOfTheFilm(film);
        filmGenreStorage.saveGenresOfTheFilm(film);
        filmDirectorStorage.saveDirectorsOfTheFilm(film);

        return getById(newFilm.getId());
    }

    public Film update(Film film) {
        Film newFilm = filmStorage.saveFilm(film);
        filmGenreStorage.deleteAllGenresOfTheFilm(film);
        filmGenreStorage.saveGenresOfTheFilm(film);
        filmDirectorStorage.deleteDirectorsFromFilm(newFilm);
        filmDirectorStorage.saveDirectorsOfTheFilm(newFilm);

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
        List<FilmGenreDirector> filmGenres = filmGenreStorage.findFilmGenresOfTheFilms(filmStorage.findTopNMostLikedFilms(limit));

        Map<Long, Film> films = new HashMap<>();
        for (FilmGenreDirector filmGenre : filmGenres) {
            Film film = films.getOrDefault(filmGenre.getFilm().getId(), filmGenre.getFilm());

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }

            final Director director = filmGenre.getDirector();
            if(director != null && director.getId() != null && director.getId() != 0L) {
                film.addDirector(director);
            }

            films.put(film.getId(), film);
        }

        return new ArrayList<>(films.values());
    }

    public Genre findGenreById(Long aLong) {
        return genreStorage.findById(aLong).orElseThrow(() -> new EntityIsNotFoundException(Genre.class, aLong));
    }

    public List<Genre> findAllGenres() {
        return genreStorage.findAll();
    }

    public FilmMpaRating findRatingById(Long aLong) {
        return mpaRatingStorage.findById(aLong).orElseThrow(() -> new EntityIsNotFoundException(FilmMpaRating.class, aLong));
    }

    public List<FilmMpaRating> findAllRatings() {
        return mpaRatingStorage.findAll();
    }

    public List<Film> getFilmsFriends(Long userId, Long friendId) {

        Optional<User> userA = userStorage.findById(userId);
        Optional<User> userB = userStorage.findById(friendId);

        if (userA.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userId);
        }

        if (userB.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, friendId);
        }

        List<FilmGenre> filmGenres = filmGenreStorage.findFilmGenresOfTheFilms(filmStorage.getFilmsFriends(userId, friendId));

        Map<Long, Film> films = new HashMap<>();
        for (FilmGenre filmGenre : filmGenres) {
            Film film = films.getOrDefault(filmGenre.getFilm().getId(), filmGenre.getFilm());

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }

            films.put(film.getId(), film);
        }

        return new ArrayList<>(films.values());
    }
    
    public List<Film> getFilmByDirector(final Long directorId, final String sortBy) {
        directorService.findById(directorId);
        
        return filmStorage.getFilmByDirector(directorId, sortBy.toLowerCase());
    }

    public void removefilm(Long filmId) {
        filmStorage.deleteFilmById(filmId);
    }
}
