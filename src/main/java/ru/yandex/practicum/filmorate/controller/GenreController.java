package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping(value = "/genres")
public class GenreController {
    final private FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Genre> findAll() {
        return (List<Genre>) filmService.findAllGenres();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable Long id) {
        return filmService.findGenreById(id);
    }
}
