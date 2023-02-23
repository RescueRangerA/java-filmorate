package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    final private UserStorage userStorage;

    final private FilmStorage filmStorage;

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<User> getAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.save(user);
    }

    public User update(User user) {
        return userStorage.save(user);
    }

    public User getById(Long userId) {
        return userStorage.findById(userId).orElseThrow(() -> new EntityIsNotFoundException(User.class, userId));
    }

    public UserFriend addFriend(Long userIdA, Long userIdB) {
        User userFrom = userStorage.findById(userIdA).orElseThrow(() -> new EntityIsNotFoundException(User.class, userIdA));
        User userTo = userStorage.findById(userIdB).orElseThrow(() -> new EntityIsNotFoundException(User.class, userIdB));

        UserFriend userFriend = userStorage.saveUserFriend(new UserFriend(userFrom, userTo));

        userStorage.addEventToFeed(
                new Feed(userFriend.getFromUser().getId(),
                        EventType.FRIEND,
                        OperationType.ADD,
                        userFriend.getToUser().getId()
                )
        );

        return userFriend;
    }

    public void removeFriend(Long userIdA, Long userIdB) {
        User userFrom = userStorage.findById(userIdA).orElseThrow(() -> new EntityIsNotFoundException(User.class, userIdA));
        User userTo = userStorage.findById(userIdB).orElseThrow(() -> new EntityIsNotFoundException(User.class, userIdB));

        userStorage.deleteUserFriend(new UserFriend(userFrom, userTo));
        userStorage.addEventToFeed(new Feed(userIdA, EventType.FRIEND, OperationType.REMOVE, userIdB));
    }

    public List<User> getFriendsInCommon(Long userIdA, Long userIdB) {
        User userA = userStorage.findById(userIdA).orElseThrow(() -> new EntityIsNotFoundException(User.class, userIdA));
        User userB = userStorage.findById(userIdB).orElseThrow(() -> new EntityIsNotFoundException(User.class, userIdB));

        return userStorage.findFriendsInCommonOf2Users(userA, userB);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new EntityIsNotFoundException(User.class, userId));

        return userStorage.findFriendsOfUser(user);
    }

    public void removeUser(Long userId) {
        userStorage.deleteById(userId);
    }

    public List<Film> getRecommendedFilms(Long userId) {
        return filmStorage.getRecommendedFilms(userId);
    }

    public List<Feed> getFeedById(Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new EntityIsNotFoundException(User.class, userId));

        return userStorage.getFeedById(user.getId());
    }
}
