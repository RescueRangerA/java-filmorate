package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

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
        Optional<User> userFrom = userStorage.findById(userIdA);
        Optional<User> userTo = userStorage.findById(userIdB);

        if (userFrom.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userIdA);
        }

        if (userTo.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userIdB);
        }

        UserFriend userFriend = userStorage.saveUserFriend(new UserFriend(userFrom.get(), userTo.get()));
        userStorage.addEventToFeed(
                new Feed(userFriend.getFromUser().getId(),
                        EventType.FRIEND,
                        OperationType.ADD,
                        userFriend.getToUser().getId()));
        return userFriend;
    }

    public void removeFriend(Long userIdA, Long userIdB) {
        Optional<User> userFrom = userStorage.findById(userIdA);
        Optional<User> userTo = userStorage.findById(userIdB);

        if (userFrom.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userIdA);
        }

        if (userTo.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userIdB);
        }
        userStorage.deleteUserFriend(new UserFriend(userFrom.get(), userTo.get()));
        userStorage.addEventToFeed(new Feed(userIdA, EventType.FRIEND, OperationType.REMOVE, userIdB));
    }

    public List<User> getFriendsInCommon(Long userIdA, Long userIdB) {
        Optional<User> userA = userStorage.findById(userIdA);
        Optional<User> userB = userStorage.findById(userIdB);

        if (userA.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userIdA);
        }

        if (userB.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userIdB);
        }

        return userStorage.findFriendsInCommonOf2Users(userA.get(), userB.get());
    }

    public List<User> getFriends(Long userId) {
        Optional<User> user = userStorage.findById(userId);

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userId);
        }

        return userStorage.findFriendsOfUser(user.get());
    }

    public void removeUser(Long userId) {
        userStorage.deleteById(userId);
    }

    public List<Film> getRecommendedFilms(Long userId) {
        return filmStorage.getRecommendedFilms(userId);
    }

    public List<Feed> getFeedById(Long userId) {
        Optional<User> user = userStorage.findById(userId);
        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userId);
        }
        return userStorage.getFeedById(userId);
    }
}
