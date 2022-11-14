package ru.yandex.practicum.filmorate.storage.userfriend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.List;

class InMemoryUserFriendStorageTest {

    private InMemoryUserFriendStorage storage;

    @BeforeEach
    void beforeEach() {
        storage = new InMemoryUserFriendStorage();
    }

    @Test
    void createByUserIds() throws EntityAlreadyExistsException, FriendOfHisOwnException {
        storage.createByUserIds(1L, 2L);
        Assertions.assertEquals(1, storage.getAll().size());
    }

    @Test
    void createByUserIdsDuplicate() throws EntityAlreadyExistsException, FriendOfHisOwnException {
        storage.createByUserIds(1L, 2L);
        Assertions.assertEquals(1, storage.getAll().size());

        Assertions.assertThrows(
                EntityAlreadyExistsException.class,
                () -> storage.createByUserIds(1L, 2L)
        );
    }

    @Test
    void createByUserIdsEquals() {
        Assertions.assertThrows(
                FriendOfHisOwnException.class,
                () -> storage.createByUserIds(1L, 1L)
        );
    }

    @Test
    void deleteByUserIds() throws EntityIsNotFoundException, EntityAlreadyExistsException, FriendOfHisOwnException {
        storage.createByUserIds(1L, 2L);
        storage.deleteByUserIds(1L, 2L);
        Assertions.assertEquals(0, storage.getAll().size());
    }

    @Test
    void deleteNonExistingByUserIds() {
        Assertions.assertThrows(
                EntityIsNotFoundException.class,
                () -> storage.deleteByUserIds(1L, 2L)
        );
    }

    @Test
    void getUserIdsInCommon() throws EntityAlreadyExistsException, FriendOfHisOwnException {
        storage.createByUserIds(1L, 2L);

        storage.createByUserIds(1L, 3L);
        storage.createByUserIds(1L, 4L);

        storage.createByUserIds(2L, 5L);
        storage.createByUserIds(2L, 6L);

        storage.createByUserIds(3L, 2L);

        storage.createByUserIds(6L, 1L);

        Assertions.assertEquals(List.of(3L, 6L), storage.getUserIdsInCommon(1L, 2L));
    }
}