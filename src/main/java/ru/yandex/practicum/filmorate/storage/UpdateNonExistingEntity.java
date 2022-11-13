package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

public class UpdateNonExistingEntity extends Exception {
    public UpdateNonExistingEntity(Entity entity) {
        super(String.format("Trying to update non-existing entity '%s' with id '%d'", entity.getClass(), entity.getId()));
    }
}
