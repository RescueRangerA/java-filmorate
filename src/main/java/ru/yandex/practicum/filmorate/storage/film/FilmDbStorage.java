package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import javax.sql.DataSource;
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
            Date releaseDate = rs.getDate("film.release_date");

            return new Film(
                    rs.getLong("film.id"),
                    rs.getString("film.title"),
                    rs.getString("film.description"),
                    releaseDate != null ? releaseDate.toLocalDate() : null,
                    rs.getInt("film.duration"),
                    new MpaRatingMapper().mapRow(rs, rowNum)
            );
        }
    }

    public static class MpaRatingMapper implements RowMapper<FilmMpaRating> {
        @Override
        public FilmMpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmMpaRating(
                    rs.getLong("film_mpa_rating.id"),
                    rs.getString("film_mpa_rating.title"),
                    rs.getString("film_mpa_rating.description")
            );
        }
    }

    public static class FilmGenreMapper implements RowMapper<FilmGenre> {
        @Override
        public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmGenre(
                    new FilmMapper().mapRow(rs, rowNum),
                    new GenreDbStorage.GenreMapper().mapRow(rs, rowNum)
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<Film> findFilmsAll() {
        Iterable<FilmGenre> filmsWithGenres = jdbcTemplate.query(
                "SELECT film.*, film_mpa_rating.*, genre.* FROM film " +
                        "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                        "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                        "LEFT JOIN genre ON genre.id = film_genre.genre_id ",
                new FilmGenreMapper()
        );


        Map<Long, Film> films = new HashMap<>();
        for (FilmGenre filmGenre : filmsWithGenres) {
            Film film = films.getOrDefault(filmGenre.getFilm().getId(), filmGenre.getFilm());

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }

            films.put(film.getId(), film);
        }

        return new LinkedList<>(films.values());
    }

    @Override
    public Film saveFilm(Film entity) {
        Assert.notNull(entity, "Film must not be null.");

        if (entity.getId() == null || entity.getId() == 0L) {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement("INSERT INTO film (title, description, release_date, duration, rating_id) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
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
                    "UPDATE film SET title = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?",
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

        jdbcTemplate.update(
                "DELETE FROM film_genre WHERE film_id = ?",
                entity.getId()
        );

        if (entity.getGenres().size() > 0) {
            Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
            genres.addAll(entity.getGenres());
            entity.setGenres(genres);

            try {
                DataSource ds = jdbcTemplate.getDataSource();
                Assert.notNull(ds);
                Connection connection = ds.getConnection();
                connection.setAutoCommit(false);

                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?)"
                );

                for (Genre genre : entity.getGenres()) {
                    if ( entity.getId() == null || genre.getId() == null ) {
                        continue;
                    }

                    ps.setLong(1, entity.getId());
                    ps.setLong(2, genre.getId());
                    ps.addBatch();
                }

                ps.executeBatch();
                ps.clearBatch();
                connection.commit();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return entity;
    }

    @Override
    public Optional<Film> findFilmById(Long aLong) {
        Assert.notNull(aLong, "Film id must not be null.");

        Iterable<FilmGenre> filmsWithGenres = jdbcTemplate.query(
                "SELECT film.*, film_mpa_rating.*, genre.* FROM film " +
                        "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                        "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                        "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                        "WHERE film.id = ?",
                new FilmGenreMapper(),
                aLong
        );


        Film film = null;
        for (FilmGenre filmGenre : filmsWithGenres) {
            if (film == null) {
                film = filmGenre.getFilm();
            }

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }
        }

        return film != null ? Optional.of(film) : Optional.empty();
    }

    @Override
    public Iterable<Film> findFilmsAllById(Iterable<Long> longs) {
        Assert.notNull(longs, "Film ids must not be null.");

        List<Long> ids = StreamSupport
                .stream(longs.spliterator(), false)
                .collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        Iterable<FilmGenre> filmsWithGenres = jdbcTemplate.query(
                String.format(
                        "SELECT film.*, film_mpa_rating.*, genre.* FROM film " +
                                "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                                "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                                "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                                "WHERE film.id IN (%s)", inSql),
                new FilmGenreMapper(),
                ids.toArray()
        );


        Map<Long, Film> films = new HashMap<>();
        for (FilmGenre filmGenre : filmsWithGenres) {
            Film film = films.getOrDefault(filmGenre.getFilm().getId(), filmGenre.getFilm());

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }

            films.put(film.getId(), film);
        }

        return new LinkedList<>(films.values());
    }

    @Override
    public void deleteFilmById(Long aLong) {
        Assert.notNull(aLong, "Film id must not be null.");

        jdbcTemplate.update("DELETE FROM film WHERE id = ?", aLong);
    }

    @Override
    public void deleteFilm(Film entity) {
        Assert.notNull(entity, "Film must not be null.");

        jdbcTemplate.update("DELETE FROM film WHERE id = ?", entity.getId());
    }

    @Override
    public Iterable<FilmMpaRating> findMpaRatingsAll() {
        return jdbcTemplate.query(
                "SELECT film_mpa_rating.* FROM film_mpa_rating",
                new MpaRatingMapper()
        );
    }

    @Override
    public Optional<FilmMpaRating> findMpaRatingById(Long aLong) {
        Assert.notNull(aLong, "Rating id must not be null.");

        FilmMpaRating genre = null;

        try {
            genre = jdbcTemplate.queryForObject(
                    "SELECT film_mpa_rating.* FROM film_mpa_rating WHERE film_mpa_rating.id = ?",
                    new MpaRatingMapper(),
                    aLong
            );
        } catch (EmptyResultDataAccessException ignored) {

        }

        return genre != null ? Optional.of(genre) : Optional.empty();
    }

    @Override
    public FilmLike saveFilmLike(FilmLike entity) {
        Assert.notNull(entity, "Entity must not be null.");
        Assert.notNull(entity.getFilm().getId(), "Film id must not be null.");
        Assert.notNull(entity.getUser().getId(), "User id must not be null.");

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("INSERT INTO film_like (film_id, user_id) VALUES (?,?)");
            statement.setLong(1, entity.getFilm().getId());
            statement.setLong(2, entity.getUser().getId());
            return statement;
        });

        return entity;
    }

    @Override
    public void deleteFilmLike(FilmLike entity) {
        Assert.notNull(entity, "Entity must not be null.");
        Assert.notNull(entity.getFilm().getId(), "Film id must not be null.");
        Assert.notNull(entity.getUser().getId(), "User id must not be null.");

        jdbcTemplate.update(
                "DELETE FROM film_like WHERE film_id = ? AND user_id = ?",
                entity.getFilm().getId(),
                entity.getUser().getId()
        );
    }

    @Override
    public Iterable<Film> findTopNMostLikedFilms(Integer limit) {
        Assert.notNull(limit, "Limit must not be null.");

        if (limit < 1) {
            return List.of();
        }

        return jdbcTemplate.query(
                "SELECT film.*,film_mpa_rating.*, COUNT(film_like.film_id) as likes FROM film " +
                        "LEFT JOIN film_like ON film.id = film_like.film_id " +
                        "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                        "GROUP BY film.id " +
                        "ORDER BY likes DESC " +
                        "LIMIT ?",
                new FilmDbStorage.FilmMapper(),
                limit
        );
    }
}
