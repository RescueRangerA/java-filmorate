package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> storage = new HashMap<>();

    private long nextId = 1L;

    @Override
    public List<Film> getAll() {
        return List.copyOf(storage.values());
    }

    public Set<Film> getFirstN(Integer limit) {
        return storage.values().stream().limit(limit).collect(Collectors.toSet());
    }

    @Override
    public Film get(Long filmId) throws EntityIsNotFoundException {
        if (!storage.containsKey(filmId)) {
            throw new EntityIsNotFoundException(Film.class, filmId);
        }

        return storage.get(filmId);
    }

    @Override
    public Set<Film> getMany(Set<Long> filmIds) {
        return storage.values().stream().filter((film -> filmIds.contains(film.getId()))).collect(Collectors.toSet());
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
