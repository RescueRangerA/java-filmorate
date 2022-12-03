package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> storage = new HashMap<>();

    private long nextId = 1L;

    @Override
    public Iterable<Film> findAll() {
        return List.copyOf(storage.values());
    }

    public Iterable<Film> findFirstN(Integer limit) {
        return storage.values().stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public Film save(Film entity) {
        Assert.notNull(entity, "Entity must not be null.");

        if (entity.getId() == null || entity.getId() == 0L) {
            entity.setId(nextId++);
            storage.put(entity.getId(), entity);
        } else {
            if (!storage.containsKey(entity.getId())) {
                throw new EntityIsNotFoundException(Film.class, entity.getId());
            }

            storage.put(entity.getId(), entity);
        }

        return entity;
    }

    @Override
    public Optional<Film> findById(Long aLong) {
        Film user = storage.get(aLong);

        return user != null ? Optional.of(user) : Optional.empty();
    }

    @Override
    public Iterable<Film> findAllById(Iterable<Long> longs) {
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
    public void delete(Film entity) {
        storage.remove(entity.getId());
    }
}
