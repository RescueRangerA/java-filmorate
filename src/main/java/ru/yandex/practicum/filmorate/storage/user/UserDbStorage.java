package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class UserDbStorage implements UserStorage {
    public static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            Date birthDateParam = rs.getDate("users.birthday");

            return new User(
                    rs.getLong("users.id"),
                    rs.getString("users.email"),
                    rs.getString("users.login"),
                    rs.getString("users.name"),
                    birthDateParam != null ? birthDateParam.toLocalDate() : null
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User entity) {
        if (entity == null) {
            return null;
        }

        if (entity.getId() == null || entity.getId() == 0L) {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement("INSERT INTO users (email, login, name, birthday) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, entity.getEmail());
                statement.setString(2, entity.getLogin());
                statement.setString(3, entity.getName());
                statement.setDate(4, Date.valueOf(entity.getBirthday()));
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

            entity.setId(newId);
        } else {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                    entity.getEmail(),
                    entity.getLogin(),
                    entity.getName(),
                    entity.getBirthday(),
                    entity.getId()
            );

            if (rowsAffected == 0) {
                throw new EntityIsNotFoundException(User.class, entity.getId());
            }
        }

        return entity;
    }

    @Override
    public Optional<User> findById(Long aLong) {
        User user = null;

        try {
            user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new UserMapper(), aLong);
        } catch (EmptyResultDataAccessException ignored) {

        }

        return user != null ? Optional.of(user) : Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", new UserMapper());
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> longs) {
        List<Long> ids = StreamSupport
                .stream(longs.spliterator(), false)
                .collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM users WHERE id IN (%s)", inSql),
                new UserMapper(),
                ids.toArray()
        );
    }

    @Override
    public void deleteById(Long aLong) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", aLong);
    }

    @Override
    public void delete(User entity) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", entity.getId());
    }
}
