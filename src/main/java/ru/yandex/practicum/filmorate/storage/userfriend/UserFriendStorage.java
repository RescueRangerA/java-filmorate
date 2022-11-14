package ru.yandex.practicum.filmorate.storage.userfriend;

import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;

public interface UserFriendStorage {

    List<UserFriend> getAll();

    List<Long> getUserIdsByUserId(Long userId);

    UserFriend createByUserIds(Long userIdA, Long userIdB) throws EntityAlreadyExistsException, FriendOfHisOwnException;

    void deleteByUserIds(Long userIdA, Long userIdB) throws EntityIsNotFoundException;

    List<Long> getUserIdsInCommon(Long userIdA, Long userIdB);
}
