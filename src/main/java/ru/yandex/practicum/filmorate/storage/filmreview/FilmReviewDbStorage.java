package ru.yandex.practicum.filmorate.storage.filmreview;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.FilmReviewLike;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Component
public class FilmReviewDbStorage implements FilmReviewStorage {
    public static class FilmReviewMapper implements RowMapper<FilmReview> {
        @Override
        public FilmReview mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmReview(
                    rs.getLong("film_review.id"),
                    rs.getString("film_review.content"),
                    rs.getBoolean("film_review.positive"),
                    rs.getLong("film_review.film_id"),
                    rs.getLong("film_review.user_id"),
                    rs.getInt("film_review_usefulness")
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public FilmReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FilmReview> findFilmReviewsAll(Integer count) {
        return jdbcTemplate.query(
                "SELECT film_review.*, COUNT(review_like.positive) - COUNT(review_dislike.positive) as film_review_usefulness FROM film_review " +
                        "LEFT JOIN film_review_like as review_like ON (film_review.id = review_like.film_review_id AND review_like.positive = true) " +
                        "LEFT JOIN film_review_like as review_dislike ON (film_review.id = review_dislike.film_review_id AND review_dislike.positive = false) " +
                        "GROUP BY film_review.id " +
                        "ORDER BY film_review_usefulness DESC " +
                        "LIMIT ?",
                new FilmReviewMapper(),
                count
        );
    }

    @Override
    public List<FilmReview> findFilmReviewsByFilmId(Long filmId, Integer count) {
        return jdbcTemplate.query(
                "SELECT film_review.*, COUNT(review_like.positive) - COUNT(review_dislike.positive) as film_review_usefulness FROM film_review " +
                        "LEFT JOIN film_review_like as review_like ON (film_review.id = review_like.film_review_id AND review_like.positive = true) " +
                        "LEFT JOIN film_review_like as review_dislike ON (film_review.id = review_dislike.film_review_id AND review_dislike.positive = false) " +
                        "WHERE film_review.film_id = ?" +
                        "GROUP BY film_review.id " +
                        "ORDER BY film_review_usefulness DESC " +
                        "LIMIT ?",
                new FilmReviewMapper(),
                filmId,
                count
        );
    }

    @Override
    public FilmReview saveFilmReview(FilmReview filmReviewEntity) {
        Assert.notNull(filmReviewEntity, "Film review must not be null.");

        if (filmReviewEntity.getReviewId() == null || filmReviewEntity.getReviewId() == 0L) {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement("INSERT INTO film_review (content, positive, film_id, user_id) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, filmReviewEntity.getContent());
                statement.setBoolean(2, filmReviewEntity.getIsPositive());
                statement.setLong(3, filmReviewEntity.getFilmId());
                statement.setLong(4, filmReviewEntity.getUserId());
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

            filmReviewEntity.setReviewId(newId);
        } else {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE film_review SET content = ?, positive = ? WHERE id = ?",
                    filmReviewEntity.getContent(),
                    filmReviewEntity.getIsPositive(),
                    filmReviewEntity.getReviewId()
            );

            if (rowsAffected == 0) {
                throw new EntityIsNotFoundException(FilmReview.class, filmReviewEntity.getReviewId());
            }
        }

        return filmReviewEntity;
    }

    @Override
    public Optional<FilmReview> findFilmReviewById(Long filmReviewId) {
        FilmReview filmReview = null;

        try {
            filmReview = jdbcTemplate.queryForObject(
                    "SELECT film_review.*, COUNT(review_like.positive) - COUNT(review_dislike.positive) as film_review_usefulness FROM film_review " +
                            "LEFT JOIN film_review_like as review_like ON (film_review.id = review_like.film_review_id AND review_like.positive = true) " +
                            "LEFT JOIN film_review_like as review_dislike ON (film_review.id = review_dislike.film_review_id AND review_dislike.positive = false) " +
                            "WHERE film_review.id = ?" +
                            "GROUP BY film_review.id",
                    new FilmReviewMapper(),
                    filmReviewId
            );
        } catch (EmptyResultDataAccessException ignored) {

        }

        return filmReview != null ? Optional.of(filmReview) : Optional.empty();
    }

    @Override
    public void deleteFilmReviewById(Long filmReviewId) {
        Assert.notNull(filmReviewId, "Film review id must not be null.");

        jdbcTemplate.update("DELETE FROM film_review WHERE id = ?", filmReviewId);
    }

    @Override
    public FilmReviewLike saveFilmReviewLike(FilmReviewLike filmReviewLikeEntity) {
        Assert.notNull(filmReviewLikeEntity, "Film review like entity must not be null.");
        Assert.notNull(filmReviewLikeEntity.getFilmReview().getReviewId(), "Film review id must not be null.");
        Assert.notNull(filmReviewLikeEntity.getUser().getId(), "User id must not be null.");

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("INSERT INTO film_review_like (film_review_id, user_id, positive) VALUES (?,?,?)");
            statement.setLong(1, filmReviewLikeEntity.getFilmReview().getReviewId());
            statement.setLong(2, filmReviewLikeEntity.getUser().getId());
            statement.setBoolean(3, filmReviewLikeEntity.getPositive());
            return statement;
        });

        return filmReviewLikeEntity;
    }

    @Override
    public void deleteFilmReviewLikeByFilmReviewIdAndUserId(Long filmReviewId, Long userId) {
        Assert.notNull(filmReviewId, "Film review id must not be null.");
        Assert.notNull(userId, "User id must not be null.");

        jdbcTemplate.update(
                "DELETE FROM film_review_like WHERE film_review_id = ? AND user_id = ?",
                filmReviewId,
                userId
        );
    }
}
