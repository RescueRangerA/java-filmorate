package ru.yandex.practicum.filmorate.storage.filmlike;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
    private final Map<Long, Map<Long, FilmLike>> storage = new HashMap<>();

    private final FilmStorage filmStorage;

    @Autowired
    public InMemoryFilmLikeStorage(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public List<FilmLike> findAll() {
        return new LinkedList<>(storage
                .values().stream()
                .map(Map::values)
                .reduce(
                        new LinkedList<>(),
                        (result, el) -> {
                            result.addAll(el);
                            return result;
                        }
                )
        );
    }

    @Override
    public FilmLike save(FilmLike entity) {
        Map<Long, FilmLike> filmLikesMapByUsers = storage.getOrDefault(entity.getFilm().getId(), new HashMap<>());

        if (filmLikesMapByUsers.containsKey(entity.getUser().getId())) {
            return entity;
        }

        filmLikesMapByUsers.put(entity.getUser().getId(), entity);
        storage.put(entity.getFilm().getId(), filmLikesMapByUsers);

        return entity;
    }

    @Override
    public void delete(FilmLike entity) {
        Film film = entity.getFilm();

        Map<Long, FilmLike> filmLikesMapByUsers = storage.get(film.getId());
        if (filmLikesMapByUsers == null) {
            throw new EntityIsNotFoundException(FilmLike.class, 0L);
        }

        User user = entity.getUser();

        FilmLike removedLike = filmLikesMapByUsers.remove(user.getId());

        if (removedLike == null) {
            throw new EntityIsNotFoundException(FilmLike.class, 0L);
        }
    }

    @Override
    public Iterable<Film> getFilmIdsAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(Integer limit) {
        LinkedHashMap<Long, Integer> countSummary = new LinkedHashMap<>();
        storage.forEach((key, value) -> countSummary.put(key, value.size()));

        List<Film> popularFilms = (List<Film>) filmStorage.findAllById(
                countSummary
                        .entrySet()
                        .stream()
                        .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                        .limit(limit)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList())
        );

        if (popularFilms.size() < limit) {
            for (Film film : filmStorage.findFirstN(limit)) {
                if (popularFilms.size() == limit) {
                    break;
                }

                if (!popularFilms.contains(film)) {
                    popularFilms.add(film);
                }
            }
        }

        return popularFilms;
    }
}
