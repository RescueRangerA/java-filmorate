package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.userfriend.FriendOfHisOwnException;
import ru.yandex.practicum.filmorate.storage.userfriend.UserFriendStorage;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private UserStorage userStorage;

    private UserFriendStorage userFriendStorage;

    @Autowired
    public UserService(UserStorage userStorage, UserFriendStorage userFriendStorage) {
        this.userStorage = userStorage;
        this.userFriendStorage = userFriendStorage;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) throws EntityIsNotFoundException {
        return userStorage.update(user);
    }

    public User getById(Long userId) throws EntityIsNotFoundException {
        return userStorage.getById(userId);
    }

    public UserFriend addFriend(Long userIdA, Long userIdB) throws EntityAlreadyExistsException, FriendOfHisOwnException, EntityIsNotFoundException {
        return userFriendStorage.createByUserIds(userStorage.getById(userIdA), userStorage.getById(userIdB));
    }

    public void removeFriend(Long userIdA, Long userIdB) throws EntityIsNotFoundException {
        userFriendStorage.deleteByUserIds(userStorage.getById(userIdA), userStorage.getById(userIdB));
    }

    public Set<User> getFriendsInCommon(Long userIdA, Long userIdB) throws EntityIsNotFoundException {
        return userStorage.getMany(
                userFriendStorage.getUserIdsInCommon(
                        userStorage.getById(userIdA),
                        userStorage.getById(userIdB)
                )
        );
    }

    public Set<User> getFriends(Long userId) throws EntityIsNotFoundException {
        return userStorage.getMany(userFriendStorage.getUserIdsByUser(userStorage.getById(userId)));
    }
}
