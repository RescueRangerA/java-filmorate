package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.mparating.MpaRatingDbStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class FilmDbStorage implements FilmStorage {
    public static class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Date releaseDate = rs.getDate("films.release_date");

            return new Film(
                    rs.getLong("films.id"),
                    rs.getString("films.title"),
                    rs.getString("films.description"),
                    releaseDate != null ? releaseDate.toLocalDate() : null,
                    rs.getInt("films.duration"),
                    (new MpaRatingDbStorage.MpaRatingMapper()).mapRow(rs, rowNum)
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<Film> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM films LEFT JOIN film_mpa_rating as rating ON films.rating_id = rating.id",
                new FilmMapper()
        );
    }

    @Override
    public Film save(Film entity) {
        Assert.notNull(entity, "Entity must not be null.");

        if (entity.getId() == null || entity.getId() == 0L) {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement("INSERT INTO films (title, description, release_date, duration, rating_id) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, entity.getName());
                statement.setString(2, entity.getDescription());
                statement.setDate(3, Date.valueOf(entity.getReleaseDate()));
                statement.setInt(4, entity.getDuration());
                statement.setLong(5, entity.getMpa().getId() != null ? entity.getMpa().getId() : 0L);
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
                throw new RuntimeException("Cannot insert the film");
            }

            entity.setId(newId);
        } else {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?",
                    entity.getName(),
                    entity.getDescription(),
                    entity.getReleaseDate(),
                    entity.getDuration(),
                    entity.getMpa().getId(),
                    entity.getId()
            );

            if (rowsAffected == 0) {
                throw new EntityIsNotFoundException(Film.class, entity.getId());
            }
        }

        return entity;
    }

    @Override
    public Optional<Film> findById(Long aLong) {
        Film film = null;

        try {
            film = jdbcTemplate.queryForObject(
                    "SELECT * FROM films LEFT JOIN film_mpa_rating as rating ON films.rating_id = rating.id WHERE films.id = ?",
                    new FilmMapper(),
                    aLong
            );
        } catch (EmptyResultDataAccessException ignored) {

        }

        return film != null ? Optional.of(film) : Optional.empty();
    }

    @Override
    public Iterable<Film> findAllById(Iterable<Long> longs) {
        List<Long> ids = StreamSupport
                .stream(longs.spliterator(), false)
                .collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM films LEFT JOIN film_mpa_rating as rating ON films.rating_id = rating.id WHERE id IN (%s)", inSql),
                new FilmMapper(),
                ids.toArray()
        );
    }

    @Override
    public void deleteById(Long aLong) {
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", aLong);
    }

    @Override
    public void delete(Film entity) {
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", entity.getId());
    }

    @Override
    public Iterable<Film> findFirstN(Integer limit) {
        return jdbcTemplate.query(
                "SELECT * FROM films LEFT JOIN film_mpa_rating ON films.rating_id = film_mpa_rating.id ORDER BY films.id LIMIT ?",
                new FilmMapper(),
                limit
        );
    }
}
