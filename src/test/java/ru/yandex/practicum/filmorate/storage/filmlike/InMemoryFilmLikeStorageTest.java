package ru.yandex.practicum.filmorate.storage.filmlike;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;

class InMemoryFilmLikeStorageTest {

    private InMemoryFilmLikeStorage storage;

    @BeforeEach
    void beforeEach() {
        storage = new InMemoryFilmLikeStorage();
    }

    @Test
    void createWithFilmIdAndUserId() throws EntityAlreadyExistsException {
        storage.createWithFilmIdAndUserId(1L, 1L);
        Assertions.assertEquals(1, storage.getAll().size());
    }

    @Test
    void createWithFilmIdAndUserIdDuplicate() throws EntityAlreadyExistsException {
        storage.createWithFilmIdAndUserId(1L, 1L);
        Assertions.assertEquals(1, storage.getAll().size());

        Assertions.assertThrows(
                EntityAlreadyExistsException.class,
                () -> storage.createWithFilmIdAndUserId(1L, 1L)
        );
    }

    @Test
    void deleteByFilmIdAndUserId() throws EntityAlreadyExistsException, EntityIsNotFoundException {
        storage.createWithFilmIdAndUserId(1L, 1L);
        storage.deleteByFilmIdAndUserId(1L, 1L);
        Assertions.assertEquals(0, storage.getAll().size());
    }

    @Test
    void deleteNonExistingByFilmIdAndUserId() {
        Assertions.assertThrows(
                EntityIsNotFoundException.class,
                () -> storage.deleteByFilmIdAndUserId(1L, 1L)
        );
    }

    @Test()
    void getAllAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN() throws EntityAlreadyExistsException {
        storage.createWithFilmIdAndUserId(1L, 1L);
        storage.createWithFilmIdAndUserId(2L, 1L);
        storage.createWithFilmIdAndUserId(3L, 1L);
        storage.createWithFilmIdAndUserId(4L, 1L);
        storage.createWithFilmIdAndUserId(5L, 1L);
        storage.createWithFilmIdAndUserId(6L, 1L);
        storage.createWithFilmIdAndUserId(7L, 1L);
        storage.createWithFilmIdAndUserId(8L, 1L);
        storage.createWithFilmIdAndUserId(9L, 1L);
        storage.createWithFilmIdAndUserId(10L, 1L);
        storage.createWithFilmIdAndUserId(2L, 2L);
        storage.createWithFilmIdAndUserId(4L, 2L);
        storage.createWithFilmIdAndUserId(2L, 3L);

        Assertions.assertEquals(
                List.of(2L, 4L, 1L),
                storage.getFilmIdsAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(3)
        );
    }
}