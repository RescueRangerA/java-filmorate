package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> storage = new HashMap<>();

    private long nextId = 1L;

    @Override
    public List<Film> getAll() {
        return List.copyOf(storage.values());
    }

    public List<Film> getFirstN(Integer limit) {
        return storage.values().stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public Film get(Long filmId) throws EntityIsNotFoundException {
        Film film = storage.get(filmId);

        if (film == null) {
            throw new EntityIsNotFoundException(Film.class, filmId);
        }

        return film;
    }

    @Override
    public List<Film> getMany(List<Long> filmIds) {
        return filmIds.stream().map(storage::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Film create(Film filmEntity) {
        filmEntity.setId(nextId++);
        storage.put(filmEntity.getId(), filmEntity);

        return filmEntity;
    }

    @Override
    public Film update(Film filmEntity) throws EntityIsNotFoundException {
        if (!storage.containsKey(filmEntity.getId())) {
            throw new EntityIsNotFoundException(filmEntity);
        }

        storage.put(filmEntity.getId(), filmEntity);

        return filmEntity;
    }
}
