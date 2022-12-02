package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storage = new HashMap<>();

    private long nextId = 1L;

    @Override
    public List<User> getAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public User create(User userEntity) {
        userEntity.setId(nextId++);
        storage.put(userEntity.getId(), userEntity);

        return userEntity;
    }

    @Override
    public User update(User userEntity) throws EntityIsNotFoundException {
        if (!storage.containsKey(userEntity.getId())) {
            throw new EntityIsNotFoundException(User.class, userEntity.getId());
        }

        storage.put(userEntity.getId(), userEntity);

        return userEntity;
    }

    @Override
    public User getById(Long userId) throws EntityIsNotFoundException {
        User user = storage.get(userId);

        if (user == null) {
            throw new EntityIsNotFoundException(User.class, userId);
        }

        return user;
    }

    @Override
    public List<User> getMany(List<Long> userIds) {
        return userIds.stream().map(storage::get).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
