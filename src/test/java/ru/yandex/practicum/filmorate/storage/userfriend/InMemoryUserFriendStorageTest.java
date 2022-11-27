package ru.yandex.practicum.filmorate.storage.userfriend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

class InMemoryUserFriendStorageTest {

    private InMemoryUserFriendStorage storage;

    @BeforeEach
    void beforeEach() {
        storage = new InMemoryUserFriendStorage();
    }

    @Test
    void createByUserIds() throws EntityAlreadyExistsException, FriendOfHisOwnException {
        User userA = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User userB = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.createByUserIds(userA, userB);
        Assertions.assertEquals(1, storage.getAll().size());
    }

    @Test
    void createByUserIdsDuplicate() throws EntityAlreadyExistsException, FriendOfHisOwnException {
        User userA = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User userB = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.createByUserIds(userA, userB);
        Assertions.assertEquals(1, storage.getAll().size());

        Assertions.assertThrows(
                EntityAlreadyExistsException.class,
                () -> storage.createByUserIds(userA, userB)
        );
    }

    @Test
    void createByUserIdsEquals() {
        User userA = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertThrows(
                FriendOfHisOwnException.class,
                () -> storage.createByUserIds(userA, userA)
        );
    }

    @Test
    void deleteByUserIds() throws EntityIsNotFoundException, EntityAlreadyExistsException, FriendOfHisOwnException {
        User userA = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User userB = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.createByUserIds(userA, userB);
        storage.deleteByUserIds(userA, userB);
        Assertions.assertEquals(0, storage.getAll().size());
    }

    @Test
    void deleteNonExistingByUserIds() {
        User userA = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User userB = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertThrows(
                EntityIsNotFoundException.class,
                () -> storage.deleteByUserIds(userA, userB)
        );
    }

    @Test
    void getUserIdsInCommon() throws EntityAlreadyExistsException, FriendOfHisOwnException {
        User user1 = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user2 = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user3 = new User(3L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user4 = new User(4L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user5 = new User(5L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user6 = new User(6L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.createByUserIds(user1, user2);

        storage.createByUserIds(user1, user3);
        storage.createByUserIds(user1, user4);

        storage.createByUserIds(user2, user5);
        storage.createByUserIds(user2, user6);

        storage.createByUserIds(user3, user2);

        storage.createByUserIds(user6, user1);

        Assertions.assertEquals(List.of(3L, 6L), storage.getUserIdsInCommon(user1, user2));
    }
}