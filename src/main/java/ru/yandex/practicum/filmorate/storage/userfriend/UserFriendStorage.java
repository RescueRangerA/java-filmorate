package ru.yandex.practicum.filmorate.storage.userfriend;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;
import java.util.Set;

public interface UserFriendStorage {

    List<UserFriend> getAll();

    Set<Long> getUserIdsByUser(User user);

    UserFriend createByUserIds(User userA, User userB) throws EntityAlreadyExistsException, FriendOfHisOwnException;

    void deleteByUserIds(User userA, User userB) throws EntityIsNotFoundException;

    Set<Long> getUserIdsInCommon(User userA, User userB);
}
