package ru.yandex.practicum.filmorate.storage.userfriend;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;

public interface UserFriendStorage {

    List<UserFriend> getAll();

    List<Long> getUserIdsByUser(User user);

    UserFriend createByUserIds(User userA, User userB) throws EntityAlreadyExistsException, FriendOfHisOwnException;

    void deleteByUserIds(User userA, User userB) throws EntityIsNotFoundException;

    List<Long> getUserIdsInCommon(User userA, User userB);
}
