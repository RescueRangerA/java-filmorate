package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmreview.FilmReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

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
        FilmReview finalFilmReview = filmReview;
        filmStorage.findFilmById(filmReview.getFilmId()).orElseThrow(() -> new EntityIsNotFoundException(Film.class, finalFilmReview.getFilmId()));
        userStorage.findById(filmReview.getUserId()).orElseThrow(() -> new EntityIsNotFoundException(User.class, finalFilmReview.getUserId()));

        filmReview = filmReviewStorage.saveFilmReview(filmReview);
        filmReview = getById(filmReview.getReviewId());

        userStorage.addEventToFeed(
                new Feed(
                        filmReview.getUserId(),
                        EventType.REVIEW,
                        OperationType.ADD,
                        filmReview.getReviewId()
                )
        );

        return filmReview;
    }

    public FilmReview update(FilmReview filmReview) {
        FilmReview finalFilmReview = filmReview;
        filmStorage.findFilmById(filmReview.getFilmId()).orElseThrow(() -> new EntityIsNotFoundException(Film.class, finalFilmReview.getFilmId()));
        userStorage.findById(filmReview.getUserId()).orElseThrow(() -> new EntityIsNotFoundException(User.class, finalFilmReview.getUserId()));

        filmReview = filmReviewStorage.saveFilmReview(filmReview);
        filmReview = getById(filmReview.getReviewId());

        userStorage.addEventToFeed(
                new Feed(
                        filmReview.getUserId(),
                        EventType.REVIEW,
                        OperationType.UPDATE,
                        filmReview.getReviewId()
                )
        );

        return filmReview;
    }

    public void deleteById(Long filmReviewId) {
        FilmReview filmReview = getById(filmReviewId);

        userStorage.addEventToFeed(
                new Feed(
                        filmReview.getUserId(),
                        EventType.REVIEW,
                        OperationType.REMOVE,
                        filmReview.getReviewId()
                )
        );

        filmReviewStorage.deleteFilmReviewById(filmReviewId);
    }

    public FilmReview getById(Long filmReviewId) {
        return filmReviewStorage.findFilmReviewById(filmReviewId).orElseThrow(() -> new EntityIsNotFoundException(FilmReview.class, filmReviewId));
    }

    public FilmReviewLike addLike(Long filmReviewId, Long userId, Boolean positive) {
        FilmReview filmReview = filmReviewStorage.findFilmReviewById(filmReviewId).orElseThrow(() -> new EntityIsNotFoundException(FilmReview.class, filmReviewId));
        User user = userStorage.findById(userId).orElseThrow(() -> new EntityIsNotFoundException(User.class, userId));

        return filmReviewStorage.saveFilmReviewLike(new FilmReviewLike(filmReview, user, positive));
    }

    public void removeLike(Long filmReviewId, Long userId) {
        FilmReview filmReview = filmReviewStorage.findFilmReviewById(filmReviewId).orElseThrow(() -> new EntityIsNotFoundException(FilmReview.class, filmReviewId));
        User user = userStorage.findById(userId).orElseThrow(() -> new EntityIsNotFoundException(User.class, userId));

        filmReviewStorage.deleteFilmReviewLikeByFilmReviewIdAndUserId(filmReview.getReviewId(), user.getId());
    }
}
