package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    User save(User entity);

    Optional<User> findById(Long aLong);

    List<User> findAllById(List<Long> longs);

    void deleteById(Long aLong);

    List<User> findFriendsOfUser(User user);

    UserFriend saveUserFriend(UserFriend entity);

    void deleteUserFriend(UserFriend entity);

    List<User> findFriendsInCommonOf2Users(User userA, User userB);
}
