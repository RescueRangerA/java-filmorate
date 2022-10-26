package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(value = "/films")
public class FilmController {
    private final HashMap<Long, Film> filmService = new HashMap<>();
    private Long nextId = 1L;

    @GetMapping
    public List<Film> findAll() {
        return List.copyOf(filmService.values());
    }

    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        film.setId(nextId++);
        filmService.put(film.getId(), film);

        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> createOrUpdate(@Valid @RequestBody Film film) {
        if (!filmService.containsKey(film.getId())) {
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }

        filmService.put(film.getId(), film);

        return new ResponseEntity<>(film, HttpStatus.OK);
    }
}
