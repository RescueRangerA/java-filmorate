package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    List<User> getAll();

    User create(User userEntity);

    User update(User userEntity) throws EntityIsNotFoundException;

    User getById(Long userId) throws EntityIsNotFoundException;

    Set<User> getMany(Set<Long> userIds);
}
