package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MpaRatingDbStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

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
                    new MpaRatingDbStorage.MpaRatingMapper().mapRow(rs, rowNum)
            );
        }
    }

    public static class FilmGenreMapper implements RowMapper<FilmGenre> {
        @Override
        public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
            FilmGenre filmGenre = new FilmGenre(
                    new FilmMapper().mapRow(rs, rowNum),
                    new GenreDbStorage.GenreMapper().mapRow(rs, rowNum)
            );
            return filmGenre;
        }
    }

    public static class FilmGenreDirectorMapper implements RowMapper<FilmGenreDirector> {
        @Override
        public FilmGenreDirector mapRow(ResultSet rs, int rowNum) throws SQLException {
            FilmGenreDirector filmGenreDirector = new FilmGenreDirector(
                    new FilmMapper().mapRow(rs, rowNum),
                    new GenreDbStorage.GenreMapper().mapRow(rs, rowNum),
                    new DirectorDbStorage.DirectorMapper().mapRow(rs, rowNum)
            );
            return filmGenreDirector;
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findFilmsAll() {
        List<FilmGenreDirector> filmsWithGenres = jdbcTemplate.query(
                "SELECT film.*, film_mpa_rating.*, genre.*, director.* FROM film " +
                        "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                        "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                        "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                        "LEFT JOIN film_director ON film_director.film_id = film.id " +
                        "LEFT JOIN director ON film_director.director_id = director.id",
                new FilmGenreDirectorMapper()
        );


        Map<Long, Film> films = new HashMap<>();
        for (FilmGenreDirector filmGenre : filmsWithGenres) {
            Film film = films.getOrDefault(filmGenre.getFilm().getId(), filmGenre.getFilm());

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }

            final Director director = filmGenre.getDirector();
            if(director != null && director.getId() != null && director.getId() != 0L) {
                film.addDirector(director);
            }

            films.put(film.getId(), film);
        }

        return new LinkedList<>(films.values());
    }

    @Override
    public Film saveFilm(Film filmEntity) {
        Assert.notNull(filmEntity, "Film must not be null.");

        if (filmEntity.getId() == null || filmEntity.getId() == 0L) {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement("INSERT INTO film (title, description, release_date, duration, rating_id) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, filmEntity.getName());
                statement.setString(2, filmEntity.getDescription());
                statement.setDate(3, Date.valueOf(filmEntity.getReleaseDate()));
                statement.setInt(4, filmEntity.getDuration());
                statement.setLong(5, filmEntity.getMpa().getId() != null ? filmEntity.getMpa().getId() : 0L);
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

            filmEntity.setId(newId);
        } else {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE film SET title = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?",
                    filmEntity.getName(),
                    filmEntity.getDescription(),
                    filmEntity.getReleaseDate(),
                    filmEntity.getDuration(),
                    filmEntity.getMpa().getId(),
                    filmEntity.getId()
            );

            if (rowsAffected == 0) {
                throw new EntityIsNotFoundException(Film.class, filmEntity.getId());
            }
        }

        return filmEntity;
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        Assert.notNull(filmId, "Film id must not be null.");

        List<FilmGenreDirector> filmsWithGenres = jdbcTemplate.query(
                "SELECT film.*, film_mpa_rating.*, genre.*, director.* FROM film " +
                        "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                        "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                        "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                        "LEFT JOIN film_director ON film_director.film_id = film.id " +
                        "LEFT JOIN director ON film_director.director_id = director.id " +
                        "WHERE film.id = ?",
                new FilmGenreDirectorMapper(),
                filmId
        );


        Film film = null;
        for (FilmGenreDirector filmGenre : filmsWithGenres) {
            if (film == null) {
                film = filmGenre.getFilm();
            }

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }

            final Director director = filmGenre.getDirector();
            if (director != null && director.getId() != null && director.getId() != 0L) {
                film.addDirector(director);
            }
        }

        return film != null ? Optional.of(film) : Optional.empty();
    }

    @Override
    public List<Film> findFilmsAllById(List<Long> filmIds) {
        Assert.notNull(filmIds, "Film ids must not be null.");

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        List<FilmGenreDirector> filmsWithGenres = jdbcTemplate.query(
                String.format(
                        "SELECT film.*, film_mpa_rating.*, genre.*, director.* FROM film " +
                                "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                                "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                                "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                                "LEFT JOIN film_director ON film_director.film_id = film.id " +
                                "LEFT JOIN director ON film_director.director_id = director.id " +
                                "WHERE film.id IN (%s)", inSql),
                new FilmGenreDirectorMapper(),
                filmIds.toArray()
        );


        Map<Long, Film> films = new LinkedHashMap<>();

        for (FilmGenreDirector filmGenre : filmsWithGenres) {
            Film film = films.getOrDefault(filmGenre.getFilm().getId(), filmGenre.getFilm());

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }

            final Director director = filmGenre.getDirector();
            if (director != null && director.getId() != null && director.getId() != 0L) {
                film.addDirector(director);
            }

            films.put(film.getId(), film);
        }

        return new LinkedList<>(films.values());
    }

    @Override
    public void deleteFilmById(Long filmId) {
        Assert.notNull(filmId, "Film id must not be null.");

        jdbcTemplate.update("DELETE FROM film WHERE id = ?", filmId);
    }

    @Override
    public FilmLike saveFilmLike(FilmLike filmLikeEntity) {
        Assert.notNull(filmLikeEntity, "Entity must not be null.");
        Assert.notNull(filmLikeEntity.getFilm().getId(), "Film id must not be null.");
        Assert.notNull(filmLikeEntity.getUser().getId(), "User id must not be null.");

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("INSERT INTO film_like (film_id, user_id) VALUES (?,?)");
            statement.setLong(1, filmLikeEntity.getFilm().getId());
            statement.setLong(2, filmLikeEntity.getUser().getId());
            return statement;
        });

        return filmLikeEntity;
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
    public List<Film> findTopNMostLikedFilms(Integer limit) {
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

    public List<Film> getFilmByDirector(final Long directorId, final String sortBy) {
        Assert.notNull(directorId, "Director id must not be null.");

        String sqlQuery = "";
        if(sortBy.equals("year")) {
            sqlQuery = "SELECT film.*,  film_mpa_rating.*, genre.*, director.* FROM film " +
                    "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                    "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                    "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                    "LEFT JOIN film_director ON film_director.film_id = film.id " +
                    "LEFT JOIN director ON film_director.director_id = director.id " +
                    "WHERE director.id = ?" +
                    "ORDER BY extract(year from CAST(release_date as DATE))";

        } else if(sortBy.equals("likes")) {

            sqlQuery = "SELECT film.*,  film_mpa_rating.*, genre.*, director.* FROM film " +
                    "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                    "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                    "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                    "LEFT JOIN film_director ON film_director.film_id = film.id " +
                    "LEFT JOIN director ON film_director.director_id = director.id " +
                    "LEFT JOIN film_like ON film.id = film_like.film_id " +
                    "WHERE director.id = ? " +
                    "GROUP BY film.id, film_mpa_rating.id, genre.id, director.id " +
                    "ORDER BY COUNT(film_like.film_id)";
        }

        final List<FilmGenreDirector> filmsWithGenres = jdbcTemplate.query(
                sqlQuery,
                new FilmGenreDirectorMapper(),
                directorId
        );

        final List<Film> films = new ArrayList<>();

        for (FilmGenreDirector filmGenre : filmsWithGenres) {
            Film film = filmGenre.getFilm();

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }

            final Director director = filmGenre.getDirector();
            if (director != null && director.getId() != null && director.getId() != 0L) {
                film.addDirector(director);
            }

            films.add(film);
        }

        return films;
    }


}
