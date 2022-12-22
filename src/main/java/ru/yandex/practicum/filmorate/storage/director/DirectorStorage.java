package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> findAll();

    Optional<Director> findbyId(final Long directorId);

    Optional<Director> saveDirector(final Director director);

    Optional<Director> updateDirector(final Director director);

    void deleteDirector(final Long directorId);


}
