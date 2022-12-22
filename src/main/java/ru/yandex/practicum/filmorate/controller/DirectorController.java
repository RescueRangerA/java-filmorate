package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/directors")
public class DirectorController {

    final private DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Director findbyId(@PathVariable final Long id) {
        return directorService.findbyId(id);
    }

    @PostMapping
    public Director saveDirector(@Valid @RequestBody final Director director) {
        return directorService.saveDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody final Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable final Long id) {
        directorService.deleteDirector(id);
    }
}
