package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

public class EntityAlreadyExistsException extends Exception {
    public EntityAlreadyExistsException(Entity entity) {
        super(String.format("Entity '%s' with props '%s' already exists", entity.getClass(), entity));
    }
}
