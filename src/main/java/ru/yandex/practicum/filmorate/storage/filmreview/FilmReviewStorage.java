package ru.yandex.practicum.filmorate.storage.filmreview;

import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.FilmReviewLike;

import java.util.List;
import java.util.Optional;

public interface FilmReviewStorage {
    List<FilmReview> findFilmReviewsAll(Integer count);

    List<FilmReview> findFilmReviewsByFilmId(Long filmId, Integer count);

    FilmReview saveFilmReview(FilmReview entity);

    Optional<FilmReview> findFilmReviewById(Long aLong);

    void deleteFilmReviewById(Long aLong);

    FilmReviewLike saveFilmReviewLike(FilmReviewLike entity);

    void deleteFilmReviewLike(FilmReviewLike entity);
}
