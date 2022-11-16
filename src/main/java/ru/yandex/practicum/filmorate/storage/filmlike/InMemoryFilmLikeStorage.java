package ru.yandex.practicum.filmorate.storage.filmlike;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
    private final Map<Long, Map<Long, FilmLike>> storage = new HashMap<>();

    private long nextId = 1L;

    @Override
    public List<FilmLike> getAll() {
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
    public FilmLike createWithFilmIdAndUserId(Film film, User user) throws EntityAlreadyExistsException {
        FilmLike filmLikeEntity = new FilmLike(nextId++, film.getId(), user.getId());
        create(filmLikeEntity);

        return filmLikeEntity;
    }

    public FilmLike create(FilmLike filmLikeEntity) throws EntityAlreadyExistsException {
        Map<Long, FilmLike> filmLikesMapByUsers = storage.getOrDefault(filmLikeEntity.getFilmId(), new HashMap<>());

        if (filmLikesMapByUsers.containsKey(filmLikeEntity.getUsedId())) {
            throw new EntityAlreadyExistsException(filmLikeEntity);
        }

        filmLikesMapByUsers.put(filmLikeEntity.getUsedId(), filmLikeEntity);
        storage.put(filmLikeEntity.getFilmId(), filmLikesMapByUsers);

        return filmLikeEntity;
    }

    @Override
    public void deleteByFilmIdAndUserId(Film film, User user) throws EntityIsNotFoundException {
        Map<Long, FilmLike> filmLikesMapByUsers = storage.get(film.getId());
        if (filmLikesMapByUsers == null) {
            throw new EntityIsNotFoundException(FilmLike.class, 0L);
        }

        FilmLike removedLike = filmLikesMapByUsers.remove(user.getId());

        if (removedLike == null) {
            throw new EntityIsNotFoundException(FilmLike.class, 0L);
        }
    }

    @Override
    public List<Long> getFilmIdsAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(Integer limit) {
        LinkedHashMap<Long, Integer> countSummary = new LinkedHashMap<>();
        storage.forEach((key, value) -> countSummary.put(key, value.size()));

        return countSummary
                .entrySet()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
