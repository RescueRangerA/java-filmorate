CREATE TABLE IF NOT EXISTS account
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar NOT NULL,
    login    varchar NOT NULL,
    name     varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS account_friendship
(
    from_user_id INTEGER REFERENCES account (id),
    to_user_id   INTEGER REFERENCES account (id)
);

CREATE TABLE IF NOT EXISTS film_mpa_rating
(
    id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title       VARCHAR      NOT NULL,
    description VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS film
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title        VARCHAR      NOT NULL,
    description  VARCHAR(200) NOT NULL,
    release_date DATE         NOT NULL,
    duration     INTEGER      NOT NULL,
    rating_id    INTEGER REFERENCES film_mpa_rating (id)
);

CREATE TABLE IF NOT EXISTS genre
(
    id    INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER REFERENCES film (id),
    genre_id INTEGER REFERENCES genre (id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS film_like
(
    film_id INTEGER REFERENCES film (id),
    user_id INTEGER REFERENCES account (id),
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS film_review
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content  VARCHAR NOT NULL,
    positive BOOLEAN NOT NULL,
    film_id  INTEGER REFERENCES film (id) ON DELETE CASCADE ON UPDATE CASCADE,
    user_id  INTEGER REFERENCES account (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS film_review_like
(
    film_review_id INTEGER REFERENCES film_review (id) ON DELETE CASCADE ON UPDATE CASCADE,
    user_id        INTEGER REFERENCES account (id) ON DELETE CASCADE ON UPDATE CASCADE,
    positive       BOOLEAN NOT NULL,
    PRIMARY KEY (film_review_id, user_id)
);