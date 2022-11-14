package ru.yandex.practicum.filmorate.storage.filmlike;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
    private final Map<Long, FilmLike> storage = new HashMap<>();

    private long nextId = 1L;

    static class FilmLikeByFilmIdAndUserIdPredicate implements Predicate<FilmLike> {
        private final Long filmId;
        private final Long userId;

        public FilmLikeByFilmIdAndUserIdPredicate(Long filmId, Long userId) {
            this.filmId = filmId;
            this.userId = userId;
        }

        @Override
        public boolean test(FilmLike filmLike) {
            return Objects.equals(filmLike.getFilmId(), filmId)
                    && Objects.equals(filmLike.getUsedId(), userId);
        }
    }

    @Override
    public List<FilmLike> getAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public FilmLike createWithFilmIdAndUserId(Long filmId, Long usedId) throws EntityAlreadyExistsException {
        FilmLike filmLikeEntity = new FilmLike(nextId++, filmId, usedId);
        create(filmLikeEntity);

        return filmLikeEntity;
    }

    public FilmLike create(FilmLike filmLikeEntity) throws EntityAlreadyExistsException {
        if (storage.containsKey(filmLikeEntity.getId())) {
            throw new EntityAlreadyExistsException(filmLikeEntity);
        }

        if (
                storage.values().stream().anyMatch(
                        new FilmLikeByFilmIdAndUserIdPredicate(filmLikeEntity.getFilmId(), filmLikeEntity.getUsedId())
                )
        ) {
            throw new EntityAlreadyExistsException(filmLikeEntity);
        }

        storage.put(filmLikeEntity.getId(), filmLikeEntity);

        return filmLikeEntity;
    }

    @Override
    public void deleteByFilmIdAndUserId(Long filmId, Long usedId) throws EntityIsNotFoundException {
        Optional<FilmLike> entityToDelete = storage
                .values()
                .stream()
                .filter(new FilmLikeByFilmIdAndUserIdPredicate(filmId, usedId))
                .findFirst();

        if (entityToDelete.isEmpty()) {
            throw new EntityIsNotFoundException(new FilmLike(0L, filmId, usedId));
        }

        storage.remove(entityToDelete.get().getId());
    }

    @Override
    public List<Long> getFilmIdsAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(Integer limit) {
        return storage
                .values()
                .stream()
                .collect(Collectors.groupingBy(FilmLike::getFilmId))
                .values()
                .stream()
                .sorted((a, b) -> Integer.compare(b.size(), a.size()))
                .limit(limit)
                .map(x -> x.get(0).getFilmId())
                .collect(Collectors.toList());
    }
}
