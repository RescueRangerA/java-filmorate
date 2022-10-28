package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(value = "/users")
public class UserController {
    private final HashMap<Long, User> userService = new HashMap<>();
    private long nextId = 1L;

    @GetMapping
    public List<User> findAll() {
        return List.copyOf(userService.values());
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        user.setId(nextId++);
        userService.put(user.getId(), user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> createOrUpdate(@Valid @RequestBody User user) {
        if (!userService.containsKey(user.getId())) {
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }

        userService.put(user.getId(), user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
