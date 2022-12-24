package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.FilmReviewLike;
import ru.yandex.practicum.filmorate.service.FilmReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping(value = "/reviews")
public class FilmReviewController {
    final private FilmReviewService filmReviewService;

    @Autowired
    public FilmReviewController(FilmReviewService filmReviewService) {
        this.filmReviewService = filmReviewService;
    }

    @GetMapping
    public List<FilmReview> findMany(@RequestParam @Nullable Long filmId, @RequestParam(defaultValue = "10") Integer count) {
        return filmId == null ? filmReviewService.findAll(count) : filmReviewService.findAllByFilmId(filmId, count);
    }

    @PostMapping
    public FilmReview createFilmReview(@Valid @RequestBody FilmReview filmReview) {
        filmReviewService.create(filmReview);

        return getFilmReview(filmReview.getReviewId());
    }

    @PutMapping
    public FilmReview updateFilmReview(@Valid @RequestBody FilmReview filmReview) {
        filmReviewService.update(filmReview);

        return getFilmReview(filmReview.getReviewId());
    }

    @DeleteMapping("/{filmReviewId}")
    public void deleteFilmReview(@PathVariable Long filmReviewId) {
        filmReviewService.deleteById(filmReviewId);
    }

    @GetMapping("/{filmReviewId}")
    public FilmReview getFilmReview(@PathVariable Long filmReviewId) {
        return filmReviewService.getById(filmReviewId);
    }

    @PutMapping("/{filmReviewId}/like/{userId}")
    public FilmReviewLike addFilmReviewLike(@PathVariable Long filmReviewId, @PathVariable Long userId) {
        return filmReviewService.addLike(filmReviewId, userId, true);
    }

    @PutMapping("/{filmReviewId}/dislike/{userId}")
    public FilmReviewLike addFilmReviewDislike(@PathVariable Long filmReviewId, @PathVariable Long userId) {
        return filmReviewService.addLike(filmReviewId, userId, false);
    }

    @DeleteMapping("/{filmReviewId}/like/{userId}")
    public void removeFilmReviewLike(@PathVariable Long filmReviewId, @PathVariable Long userId) {
        filmReviewService.removeLike(filmReviewId, userId);
    }

    @DeleteMapping("/{filmReviewId}/dislike/{userId}")
    public void removeFilmReviewDisLike(@PathVariable Long filmReviewId, @PathVariable Long userId) {
        filmReviewService.removeLike(filmReviewId, userId);
    }
}
