package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    final private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
        userStorage.addEventToFeed(new Feed(userIdA, EventType.FRIEND, OperationType.ADD, userIdB));
        return userStorage.saveUserFriend(new UserFriend(userFrom.get(), userTo.get()));
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
        userStorage.addEventToFeed(new Feed(userIdA, EventType.FRIEND, OperationType.REMOVE, userIdB));
        userStorage.deleteUserFriend(new UserFriend(userFrom.get(), userTo.get()));
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

    public List<Feed> getFeedById(Long userId) {
        Optional<User> user = userStorage.findById(userId);
        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, userId);
        }
        return userStorage.getFeedById(userId);
    }
}
