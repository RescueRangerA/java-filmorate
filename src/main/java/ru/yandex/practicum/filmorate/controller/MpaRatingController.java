package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping(value = "/mpa")
public class MpaRatingController {
    final private FilmService filmService;

    @Autowired
    public MpaRatingController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<FilmMpaRating> findAll() {
        return (List<FilmMpaRating>) filmService.findAllRatings();
    }

    @GetMapping("/{id}")
    public FilmMpaRating findById(@PathVariable Long id) {
        return filmService.findRatingById(id);
    }
}
