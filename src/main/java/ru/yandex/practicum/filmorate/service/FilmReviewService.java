package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmreview.FilmReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class FilmReviewService {
    final private FilmReviewStorage filmReviewStorage;

    final private UserStorage userStorage;
    final private FilmStorage filmStorage;

    @Autowired
    public FilmReviewService(FilmReviewStorage filmReviewStorage, UserStorage userStorage, FilmStorage filmStorage) {
        this.filmReviewStorage = filmReviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<FilmReview> findAll(Integer count) {
        return filmReviewStorage.findFilmReviewsAll(count);
    }

    public List<FilmReview> findAllByFilmId(Long filmId, Integer count) {
        return filmReviewStorage.findFilmReviewsByFilmId(filmId, count);
    }

    public FilmReview create(FilmReview filmReview) {
        Optional<Film> film = filmStorage.findFilmById(filmReview.getFilmId());
        Optional<User> user = userStorage.findById(filmReview.getUserId());

        if (film.isEmpty()) {
            throw new EntityIsNotFoundException(Film.class, filmReview.getFilmId());
        }

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, filmReview.getUserId());
        }
        userStorage.addEventToFeed(new Feed(filmReview.getUserId(), EventType.REVIEW,
                OperationType.ADD, filmReview.getFilmId()));
        return filmReviewStorage.saveFilmReview(filmReview);
    }

    public FilmReview update(FilmReview filmReview) {
        Optional<Film> film = filmStorage.findFilmById(filmReview.getFilmId());
        Optional<User> user = userStorage.findById(filmReview.getUserId());

        if (film.isEmpty()) {
            throw new EntityIsNotFoundException(Film.class, filmReview.getFilmId());
        }

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, filmReview.getUserId());
        }
        userStorage.addEventToFeed(new Feed(filmReview.getUserId(), EventType.REVIEW,
                OperationType.UPDATE, filmReview.getFilmId()));
        return filmReviewStorage.saveFilmReview(filmReview);
    }

    public void deleteById(Long filmReviewId) {
        filmReviewStorage.deleteFilmReviewById(filmReviewId);
    }

    public FilmReview getById(Long filmReviewId) {
        return filmReviewStorage.findFilmReviewById(filmReviewId).orElseThrow(() -> new EntityIsNotFoundException(FilmReview.class, filmReviewId));
    }

    public FilmReviewLike addLike(Long filmReviewId, Long userId, Boolean positive) {
        Optional<FilmReview> filmReview = filmReviewStorage.findFilmReviewById(filmReviewId);
        Optional<User> user = userStorage.findById(userId);

        if (filmReview.isEmpty()) {
            throw new EntityIsNotFoundException(FilmReview.class, filmReviewId);
        }

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userId);
        }
        userStorage.addEventToFeed(new Feed(userId, EventType.REVIEW, OperationType.ADD, filmReviewId));
        return filmReviewStorage.saveFilmReviewLike(new FilmReviewLike(filmReview.get(), user.get(), positive));
    }

    public void removeLike(Long filmReviewId, Long userId) {
        Optional<FilmReview> filmReview = filmReviewStorage.findFilmReviewById(filmReviewId);
        Optional<User> user = userStorage.findById(userId);

        if (filmReview.isEmpty()) {
            throw new EntityIsNotFoundException(FilmReview.class, filmReviewId);
        }

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userId);
        }
        userStorage.addEventToFeed(new Feed(userId, EventType.REVIEW, OperationType.REMOVE, filmReviewId));
        filmReviewStorage.deleteFilmReviewLikeByFilmReviewIdAndUserId(filmReviewId, userId);
    }
}
