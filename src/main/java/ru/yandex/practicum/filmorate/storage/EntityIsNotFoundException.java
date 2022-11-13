package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

public class EntityIsNotFoundException extends Exception {
    public EntityIsNotFoundException(Entity entity) {
        super(String.format("Entity '%s' with id '%d' is not found", entity.getClass(), entity.getId()));
    }
}
