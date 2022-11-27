package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping(value = "/films")
public class FilmController {
    final private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film createOrUpdate(@Valid @RequestBody Film film) throws EntityIsNotFoundException {
        return filmService.update(film);
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable Long filmId) throws EntityIsNotFoundException {
        return filmService.getById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public FilmLike addLike(@PathVariable Long filmId, @PathVariable Long userId) throws EntityAlreadyExistsException, EntityIsNotFoundException {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) throws EntityIsNotFoundException {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }
}
