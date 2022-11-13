package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UpdateNonExistingEntity;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User create(User userEntity);

    User update(User userEntity) throws UpdateNonExistingEntity;
}
