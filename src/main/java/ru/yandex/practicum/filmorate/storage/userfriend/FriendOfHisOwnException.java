package ru.yandex.practicum.filmorate.storage.userfriend;

import ru.yandex.practicum.filmorate.model.UserFriend;

public class FriendOfHisOwnException extends Exception {
    public FriendOfHisOwnException(UserFriend userFriendEntity) {
        super(String.format("%s entity with equal props 'userId' is not allowed '%s'", userFriendEntity.getClass(), userFriendEntity));
    }
}
