package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.userfriend.InMemoryUserFriendStorage;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class UserControllerTest {
    private UserController controller;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @BeforeEach
    void beforeEach() {
        controller = new UserController(new UserService(new InMemoryUserStorage(), new InMemoryUserFriendStorage()));
    }

    @Test
    void create() {
        User user = new User(null, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertEquals(Set.of(), validator.validate(user));
        User createdUser = controller.create(user);

        Assertions.assertEquals(1L, createdUser.getUserId());
        assertEqualsUsers(user, createdUser);
    }

    @Test
    void validateFailLogin() {
        User user = new User(null, "yandex@mail.ru", "dolore ullamco", "", LocalDate.parse("2000-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertEquals(1, validator.validate(user).size());
    }

    @Test
    void validateFailEmail() {
        User user = new User(null, "mail.ru", "dolore", "", LocalDate.parse("1980-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertEquals(1, validator.validate(user).size());
    }

    @Test
    void validateFailBirthday() {
        User user = new User(null, "test@mail.ru", "dolore", "", LocalDate.parse("2446-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertEquals(1, validator.validate(user).size());
    }

    @Test
    void update() {
        User user = new User(null, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertEquals(Set.of(), validator.validate(user));
        User createdUser = controller.create(user);

        Assertions.assertEquals(1L, createdUser.getUserId());
        assertEqualsUsers(user, createdUser);

        User updateUser = new User(1L, "mail@yandex.ru", "doloreUpdate", "est adipisicing", LocalDate.parse("1976-09-20", DateTimeFormatter.ISO_DATE));
        Assertions.assertEquals(Set.of(), validator.validate(user));

        AtomicReference<User> updatedUser = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> updatedUser.set(controller.createOrUpdate(updateUser)));
        Assertions.assertEquals(updateUser, updatedUser.get());
    }

    @Test
    void updateUnknown() {
        User user = new User(null, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertEquals(Set.of(), validator.validate(user));
        User createdUser = controller.create(user);

        Assertions.assertEquals(1L, createdUser.getUserId());
        assertEqualsUsers(user, createdUser);

        User updateUser = new User(9999L, "mail@yandex.ru", "doloreUpdate", "est adipisicing", LocalDate.parse("1976-09-20", DateTimeFormatter.ISO_DATE));
        Assertions.assertEquals(Set.of(), validator.validate(user));

        Assertions.assertThrows(EntityIsNotFoundException.class, () -> controller.createOrUpdate(updateUser));
    }

    @Test
    void createEmptyName() {
        User user = new User(null, "friend@common.ru", "common", "", LocalDate.parse("2000-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertEquals(Set.of(), validator.validate(user));
        User createdUser = controller.create(user);

        assertEqualsUsers(user, createdUser);
        Assertions.assertEquals(createdUser.getLogin(), createdUser.getName());
    }

    void assertEqualsUsers(User userExpected, User userActual) {
        Assertions.assertEquals(userExpected.getLogin(), userActual.getLogin());
        Assertions.assertEquals(userExpected.getEmail(), userActual.getEmail());
        Assertions.assertEquals(userExpected.getName(), userActual.getName());
        Assertions.assertEquals(userExpected.getBirthday(), userActual.getBirthday());
    }
}
