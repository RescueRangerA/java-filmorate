package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class UserDbStorage implements UserStorage {
    public static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            Date birthDateParam = rs.getDate("account.birthday");

            return new User(
                    rs.getLong("account.id"),
                    rs.getString("account.email"),
                    rs.getString("account.login"),
                    rs.getString("account.name"),
                    birthDateParam != null ? birthDateParam.toLocalDate() : null
            );
        }
    }

    public static class UserFriendMapper implements RowMapper<UserFriend> {
        @Override
        public UserFriend mapRow(ResultSet rs, int rowNum) throws SQLException {
            Date userFromBirthDateParam = rs.getDate("user_from.birthday");

            User userFrom = new User(
                    rs.getLong("user_from.id"),
                    rs.getString("user_from.email"),
                    rs.getString("user_from.login"),
                    rs.getString("user_from.name"),
                    userFromBirthDateParam != null ? userFromBirthDateParam.toLocalDate() : null
            );

            Date userToBirthDateParam = rs.getDate("user_to.birthday");

            User userTo = new User(
                    rs.getLong("user_to.id"),
                    rs.getString("user_to.email"),
                    rs.getString("user_to.login"),
                    rs.getString("user_to.name"),
                    userToBirthDateParam != null ? userToBirthDateParam.toLocalDate() : null
            );

            return new UserFriend(userFrom, userTo);
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User userEntity) {
        Assert.notNull(userEntity, "User must not be null.");

        if (userEntity.getId() == null || userEntity.getId() == 0L) {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement("INSERT INTO account (email, login, name, birthday) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, userEntity.getEmail());
                statement.setString(2, userEntity.getLogin());
                statement.setString(3, userEntity.getName());
                statement.setDate(4, Date.valueOf(userEntity.getBirthday()));
                return statement;
            }, holder);


            // https://stackoverflow.com/a/32361613
            Long newId = null;
            if (holder.getKeys() != null && holder.getKeys().size() > 1) {
                newId = (Long) holder.getKeys().get("id");
            } else if (holder.getKey() != null) {
                newId = holder.getKey().longValue();
            }

            if (newId == null) {
                throw new RuntimeException("Cannot insert the user");
            }

            userEntity.setId(newId);
        } else {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE account SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                    userEntity.getEmail(),
                    userEntity.getLogin(),
                    userEntity.getName(),
                    userEntity.getBirthday(),
                    userEntity.getId()
            );

            if (rowsAffected == 0) {
                throw new EntityIsNotFoundException(User.class, userEntity.getId());
            }
        }

        return userEntity;
    }

    @Override
    public Optional<User> findById(Long userId) {
        Assert.notNull(userId, "User id must not be null.");

        User user = null;

        try {
            user = jdbcTemplate.queryForObject("SELECT account.* FROM account WHERE id = ?", new UserMapper(), userId);
        } catch (EmptyResultDataAccessException ignored) {

        }

        return user != null ? Optional.of(user) : Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        return jdbcTemplate.query("SELECT account.* FROM account", new UserMapper());
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> userIds) {
        Assert.notNull(userIds, "User ids must not be null.");

        List<Long> ids = StreamSupport
                .stream(userIds.spliterator(), false)
                .collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT account.* FROM account WHERE id IN (%s)", inSql),
                new UserMapper(),
                ids.toArray()
        );
    }

    @Override
    public void deleteById(Long userId) {
        Assert.notNull(userId, "User id must not be null.");

        jdbcTemplate.update("DELETE FROM account WHERE id = ?", userId);
    }

    @Override
    public void delete(User userEntity) {
        Assert.notNull(userEntity, "User must not be null.");

        jdbcTemplate.update("DELETE FROM account WHERE id = ?", userEntity.getId());
    }

    @Override
    public Iterable<UserFriend> findUserFriendAll() {
        return jdbcTemplate.query(
                "SELECT user_from.*, user_to.* FROM account_friendship " +
                        "LEFT JOIN account as user_from ON user_from.id = account_friendship.from_user_id " +
                        "LEFT JOIN account as user_to ON user_to.id = account_friendship.to_user_id ",
                new UserFriendMapper()
        );
    }

    @Override
    public Iterable<User> findFriendsOfUser(User userEntity) {
        Assert.notNull(userEntity, "User must not be null.");

        return jdbcTemplate.query(
                "SELECT account.* FROM account " +
                        "LEFT JOIN account_friendship as uf_to ON account.id = uf_to.to_user_id " +
                        "WHERE (uf_to.from_user_id = ?)",
                new UserMapper(),
                userEntity.getId()
        );
    }

    @Override
    public UserFriend saveUserFriend(UserFriend userFriendEntity) {
        Assert.notNull(userFriendEntity, "Entity must not be null.");
        Assert.notNull(userFriendEntity.getFromUser().getId(), "From user id must not be null.");
        Assert.notNull(userFriendEntity.getToUser().getId(), "To user id must not be null.");
        Assert.isTrue(
                !Objects.equals(userFriendEntity.getFromUser().getId(), userFriendEntity.getToUser().getId()),
                "Equal from user id and to user id are not allowed"
        );

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("INSERT INTO account_friendship (from_user_id, to_user_id) VALUES (?,?)");
            statement.setLong(1, userFriendEntity.getFromUser().getId());
            statement.setLong(2, userFriendEntity.getToUser().getId());
            return statement;
        });

        return userFriendEntity;
    }

    @Override
    public void deleteUserFriend(UserFriend userFriendEntity) {
        Assert.notNull(userFriendEntity, "Entity must not be null.");
        Assert.notNull(userFriendEntity.getFromUser().getId(), "From user id must not be null.");
        Assert.notNull(userFriendEntity.getToUser().getId(), "To user id must not be null.");

        jdbcTemplate.update(
                "DELETE FROM account_friendship WHERE from_user_id IN (?,?) AND to_user_id IN (?,?)",
                userFriendEntity.getFromUser().getId(),
                userFriendEntity.getToUser().getId(),
                userFriendEntity.getFromUser().getId(),
                userFriendEntity.getToUser().getId()
        );
    }

    @Override
    public Iterable<User> findFriendsInCommonOf2Users(User userEntityA, User userEntityB) {
        Assert.notNull(userEntityA, "User must not be null.");
        Assert.notNull(userEntityB, "User must not be null.");

        return jdbcTemplate.query(
                "SELECT account.* FROM account " +
                        "LEFT JOIN account_friendship as uf_to ON account.id = uf_to.to_user_id " +
                        "WHERE uf_to.from_user_id IN (?,?) " +
                        "GROUP BY account.id " +
                        "HAVING COUNT(account.id) = 2",
                new UserMapper(),
                userEntityA.getId(),
                userEntityB.getId()
        );
    }
}
