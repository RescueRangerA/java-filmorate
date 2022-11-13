package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> storage = new HashMap<>();

    private long nextId = 1L;

    public List<Film> getAll() {
        return List.copyOf(storage.values());
    }

    public Film create(Film filmEntity) {
        filmEntity.setId(nextId++);
        storage.put(filmEntity.getId(), filmEntity);

        return filmEntity;
    }

    public Film update(Film filmEntity) throws EntityIsNotFoundException {
        if (!storage.containsKey(filmEntity.getId())) {
            throw new EntityIsNotFoundException(filmEntity);
        }

        storage.put(filmEntity.getId(), filmEntity);

        return filmEntity;
    }
}
