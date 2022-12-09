package ru.yandex.practicum.filmorate.storage.userfriend;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

public interface UserFriendStorage {

    Iterable<UserFriend> findAll();

    Iterable<User> findFriendsOfUser(User user);

    UserFriend save(UserFriend entity) throws FriendOfHisOwnException;

    void delete(UserFriend entity);

    Iterable<User> findFriendsInCommonOf2Users(User userA, User userB);
}
