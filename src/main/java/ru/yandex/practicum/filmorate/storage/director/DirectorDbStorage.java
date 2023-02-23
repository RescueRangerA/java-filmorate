package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static class DirectorMapper implements RowMapper<Director> {

        @Override
        public Director mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            return new Director(
                    rs.getLong("director.id"),
                    rs.getString("director.name"));
        }
    }

    @Override
    public List<Director> findAll() {
        final String sqlQuery = "SELECT * FROM director";

        return jdbcTemplate.query(
                sqlQuery,
                new DirectorMapper()
        );
    }

    @Override
    public Optional<Director> findById(final Long directorId) {
        final String sqlQuery = "SELECT * FROM director WHERE id = ?";
        Director director = null;

        try {
            director = jdbcTemplate.queryForObject(
                    sqlQuery,
                    new DirectorMapper(),
                    directorId
            );
        } catch (EmptyResultDataAccessException e) {
        }

        return Optional.ofNullable(director);
    }

    @Override
    public Optional<Director> saveDirector(Director director) {
        final String sqlQuery = "INSERT INTO director(name) VALUES (?);";
        GeneratedKeyHolder holder = new GeneratedKeyHolder();

        int queryResult = jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, director.getName());
            return statement;
        }, holder);

        if (queryResult == 1) {
            director.setId(holder.getKey().longValue());
            return Optional.of(director);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Director> updateDirector(Director director) {
        final String sqlQuery = "UPDATE director SET name=? WHERE id=?";

        int queryResult = jdbcTemplate.update(
                sqlQuery,
                director.getName(),
                director.getId()
        );
        return queryResult == 1 ? Optional.of(director) : Optional.empty();
    }

    @Override
    public void deleteDirector(Long directorId) {
        final String sqlQuery = "DELETE FROM director WHERE id = ?;";

        jdbcTemplate.update(
                sqlQuery,
                directorId
        );
    }
}
