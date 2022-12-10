package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

import java.util.Optional;

public interface UserStorage {
    Iterable<User> findAll();

    User save(User entity);

    Optional<User> findById(Long aLong);

    Iterable<User> findAllById(Iterable<Long> longs);

    void deleteById(Long aLong);

    void delete(User entity);

    Iterable<UserFriend> findUserFriendAll();

    Iterable<User> findFriendsOfUser(User user);

    UserFriend saveUserFriend(UserFriend entity);

    void deleteUserFriend(UserFriend entity);

    Iterable<User> findFriendsInCommonOf2Users(User userA, User userB);
}
