package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {

    final private DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(final Long id) {
        return directorStorage
                .findById(id)
                .orElseThrow(
                    ()-> new EntityIsNotFoundException(Director.class, id)
                );
    }

    public Director saveDirector(final Director director) {
        return directorStorage.saveDirector(director).orElseThrow(
                ()-> new RuntimeException("Cannot insert the director")
        );
    }

    public Director updateDirector(final Director director) {
        findById(director.getId());

        return directorStorage
                .updateDirector(director)
                .orElseThrow(
                    ()-> new RuntimeException("Cannot update the director")
                );
    }

    public void deleteDirector(final Long id) {
        directorStorage.deleteDirector(id);
    }
}
