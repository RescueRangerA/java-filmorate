package ru.yandex.practicum.filmorate.storage.userfriend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Component
public class UserFriendDbStorage implements UserFriendStorage {
    public static class UserFriendMapper implements RowMapper<UserFriend> {
        @Override
        public UserFriend mapRow(ResultSet rs, int rowNum) throws SQLException {
            Date userFromBirthDateParam = rs.getDate("users_from.birthday");

            User userFrom = new User(
                    rs.getLong("users_from.id"),
                    rs.getString("users_from.email"),
                    rs.getString("users_from.login"),
                    rs.getString("users_from.name"),
                    userFromBirthDateParam != null ? userFromBirthDateParam.toLocalDate() : null
            );

            Date userToBirthDateParam = rs.getDate("users_to.birthday");

            User userTo = new User(
                    rs.getLong("users_to.id"),
                    rs.getString("users_to.email"),
                    rs.getString("users_to.login"),
                    rs.getString("users_to.name"),
                    userToBirthDateParam != null ? userToBirthDateParam.toLocalDate() : null
            );

            return new UserFriend(userFrom, userTo);
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public UserFriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<UserFriend> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM user_friendship " +
                        "LEFT JOIN users as users_from ON users_from.id = user_friendship.from_user_id " +
                        "LEFT JOIN users as users_to ON users_to.id = user_friendship.to_user_id ",
                new UserFriendMapper()
        );
    }

    @Override
    public Iterable<User> findFriendsOfUser(User user) {
        return jdbcTemplate.query(
                "SELECT * FROM users " +
                        "LEFT JOIN user_friendship as uf_to ON users.id = uf_to.to_user_id " +
                        "WHERE (uf_to.from_user_id = ?)",
                new UserDbStorage.UserMapper(),
                user.getId()
        );
    }

    @Override
    public UserFriend save(UserFriend entity) {
        Assert.notNull(entity, "Entity must not be null.");
        Assert.notNull(entity.getFromUser().getId(), "From user id must not be null.");
        Assert.notNull(entity.getToUser().getId(), "To user id must not be null.");
        Assert.isTrue(
                !Objects.equals(entity.getFromUser().getId(), entity.getToUser().getId()),
                "Equal from user id and to user id are not allowed"
        );

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("INSERT INTO user_friendship (from_user_id, to_user_id) VALUES (?,?) ON CONFLICT DO NOTHING");
            statement.setLong(1, entity.getFromUser().getId());
            statement.setLong(2, entity.getToUser().getId());
            return statement;
        });

        return entity;
    }

    @Override
    public void delete(UserFriend entity) {
        Assert.notNull(entity, "Entity must not be null.");
        Assert.notNull(entity.getFromUser().getId(), "From user id must not be null.");
        Assert.notNull(entity.getToUser().getId(), "To user id must not be null.");

        jdbcTemplate.update(
                "DELETE FROM user_friendship WHERE from_user_id IN (?,?) AND to_user_id IN (?,?)",
                entity.getFromUser().getId(),
                entity.getToUser().getId(),
                entity.getFromUser().getId(),
                entity.getToUser().getId()
        );
    }

    @Override
    public Iterable<User> findFriendsInCommonOf2Users(User userA, User userB) {
        return jdbcTemplate.query(
                "SELECT users.* FROM users " +
                        "LEFT JOIN user_friendship as uf_to ON users.id = uf_to.to_user_id " +
                        "WHERE uf_to.from_user_id IN (?,?) " +
                        "GROUP BY users.id " +
                        "HAVING COUNT(users.id) = 2",
                new UserDbStorage.UserMapper(),
                userA.getId(),
                userB.getId()
        );
    }
}
