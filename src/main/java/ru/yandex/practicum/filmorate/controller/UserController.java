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
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping(value = "/users")
public class UserController {
    private final Map<Long, User> userStorage = new HashMap<>();
    private long nextId = 1L;

    @GetMapping
    public List<User> findAll() {
        return List.copyOf(userStorage.values());
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        user.setId(nextId++);
        userStorage.put(user.getId(), user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> createOrUpdate(@Valid @RequestBody User user) {
        if (!userStorage.containsKey(user.getId())) {
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }

        userStorage.put(user.getId(), user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
