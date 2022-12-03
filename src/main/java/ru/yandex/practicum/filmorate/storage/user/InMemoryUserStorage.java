package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storage = new HashMap<>();

    private long nextId = 1L;

    @Override
    public List<User> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public User save(User entity) {
        Assert.notNull(entity, "Entity must not be null.");

        if (entity.getId() == null || entity.getId() == 0L) {
            entity.setId(nextId++);
            storage.put(entity.getId(), entity);
        } else {
            if (!storage.containsKey(entity.getId())) {
                throw new EntityIsNotFoundException(User.class, entity.getId());
            }

            storage.put(entity.getId(), entity);
        }

        return entity;
    }

    @Override
    public Optional<User> findById(Long aLong) {
        User user = storage.get(aLong);

        return user != null ? Optional.of(user) : Optional.empty();
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> longs) {
        return StreamSupport
                .stream(longs.spliterator(), false)
                .map(storage::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long aLong) {
        storage.remove(aLong);
    }

    @Override
    public void delete(User entity) {
        storage.remove(entity.getId());
    }
}
