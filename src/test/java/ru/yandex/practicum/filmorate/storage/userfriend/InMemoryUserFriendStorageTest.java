package ru.yandex.practicum.filmorate.storage.userfriend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

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
    void createByUserIds() throws FriendOfHisOwnException {
        User userA = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User userB = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.save(new UserFriend(userA, userB));
        Assertions.assertEquals(1, storage.findAll().size());
    }

    @Test
    void createByUserIdsEquals() {
        User userA = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertThrows(
                FriendOfHisOwnException.class,
                () -> storage.save(new UserFriend(userA, userA))
        );
    }

    @Test
    void deleteByUserIds() throws FriendOfHisOwnException {
        User userA = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User userB = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.save(new UserFriend(userA, userB));
        storage.delete(new UserFriend(userA, userB));
        Assertions.assertEquals(0, storage.findAll().size());
    }

    @Test
    void getUserIdsInCommon() throws FriendOfHisOwnException {
        User user1 = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user2 = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user3 = new User(3L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user4 = new User(4L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user5 = new User(5L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user6 = new User(6L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.save(new UserFriend(user1, user2));

        storage.save(new UserFriend(user1, user3));
        storage.save(new UserFriend(user3, user1));
        storage.save(new UserFriend(user1, user4));

        storage.save(new UserFriend(user2, user5));
        storage.save(new UserFriend(user2, user6));
        storage.save(new UserFriend(user6, user2));

        storage.save(new UserFriend(user3, user2));
        storage.save(new UserFriend(user2, user3));

        storage.save(new UserFriend(user6, user1));
        storage.save(new UserFriend(user1, user6));

        Assertions.assertEquals(List.of(3L, 6L), storage.findFriendsInCommonOf2Users(user1, user2));
    }
}