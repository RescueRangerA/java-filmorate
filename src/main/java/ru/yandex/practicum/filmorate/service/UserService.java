package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.userfriend.UserFriendStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    final private UserStorage userStorage;

    final private UserFriendStorage userFriendStorage;

    @Autowired
    public UserService(UserStorage userStorage, UserFriendStorage userFriendStorage) {
        this.userStorage = userStorage;
        this.userFriendStorage = userFriendStorage;
    }

    public List<User> getAll() {
        return (List<User>) userStorage.findAll();
    }

    public User save(User user) {
        return userStorage.save(user);
    }

    public User getById(Long userId) {
        return userStorage.findById(userId).orElseThrow(() -> new EntityIsNotFoundException(User.class, 0L));
    }

    public UserFriend addFriend(Long userIdA, Long userIdB) {
        Optional<User> userFrom = userStorage.findById(userIdA);
        Optional<User> userTo = userStorage.findById(userIdB);

        if (userFrom.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        if (userTo.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        return userFriendStorage.save(new UserFriend(userFrom.get(), userTo.get()));
    }

    public void removeFriend(Long userIdA, Long userIdB) {
        Optional<User> userFrom = userStorage.findById(userIdA);
        Optional<User> userTo = userStorage.findById(userIdB);

        if (userFrom.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        if (userTo.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        userFriendStorage.delete(new UserFriend(userFrom.get(), userTo.get()));
    }

    public List<User> getFriendsInCommon(Long userIdA, Long userIdB) {
        Optional<User> userA = userStorage.findById(userIdA);
        Optional<User> userB = userStorage.findById(userIdB);

        if (userA.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        if (userB.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        return (List<User>) userFriendStorage.findFriendsInCommonOf2Users(userA.get(), userB.get());
    }

    public List<User> getFriends(Long userId) {
        Optional<User> user = userStorage.findById(userId);

        if (user.isEmpty()) {
            throw new EntityIsNotFoundException(User.class, 0L);
        }

        return (List<User>) userFriendStorage.findFriendsOfUser(user.get());
    }
}
