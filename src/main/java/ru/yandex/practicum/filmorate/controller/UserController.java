package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.userfriend.FriendOfHisOwnException;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping(value = "/users")
public class UserController {
    final private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User createOrUpdate(@Valid @RequestBody User user) throws EntityIsNotFoundException {
        return userService.update(user);
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) throws EntityIsNotFoundException {
        return userService.getById(userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Long userId) throws EntityIsNotFoundException {
        return userService.getFriends(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public UserFriend addFriend(@PathVariable Long userId, @PathVariable Long friendId) throws EntityAlreadyExistsException, FriendOfHisOwnException, EntityIsNotFoundException {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId) throws EntityIsNotFoundException {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getFriends(@PathVariable Long userId, @PathVariable Long otherId) throws EntityIsNotFoundException {
        return userService.getFriendsInCommon(userId, otherId);
    }
}
