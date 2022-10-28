package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping(value = "/films")
public class FilmController {
    private final Map<Long, Film> filmStorage = new HashMap<>();
    private long nextId = 1L;

    @GetMapping
    public List<Film> findAll() {
        return List.copyOf(filmStorage.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(nextId++);
        filmStorage.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film createOrUpdate(@Valid @RequestBody Film film) throws ValidationException {
        if (!filmStorage.containsKey(film.getId())) {
            throw new ValidationException(String.format("Film with id '%d' already exists", film.getId()));
        }

        filmStorage.put(film.getId(), film);

        return film;
    }
}
