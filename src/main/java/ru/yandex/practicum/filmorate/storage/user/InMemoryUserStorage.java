package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storage = new HashMap<>();

    private long nextId = 1L;

    public List<User> getAll() {
        return List.copyOf(storage.values());
    }

    public User create(User userEntity) {
        userEntity.setId(nextId++);
        storage.put(userEntity.getId(), userEntity);

        return userEntity;
    }

    public User update(User userEntity) throws EntityIsNotFoundException {
        if (!storage.containsKey(userEntity.getId())) {
            throw new EntityIsNotFoundException(userEntity);
        }

        storage.put(userEntity.getId(), userEntity);

        return userEntity;
    }
}
