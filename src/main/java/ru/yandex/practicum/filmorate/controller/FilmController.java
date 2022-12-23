package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.service.FilmService;

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
        return filmService.findAll();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable Long filmId) {
        return filmService.getById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public FilmLike addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam(defaultValue = "title") String by){
        return filmService.getSearch(query, by);
    }

    @DeleteMapping("/{filmId}")
    public void removeUser(@PathVariable Long filmId) {
        filmService.removefilm(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmByDirector(@PathVariable Long directorId, @RequestParam(defaultValue = "year") String sortBy) {
        return filmService.getFilmByDirector(directorId, sortBy);
    }
}
