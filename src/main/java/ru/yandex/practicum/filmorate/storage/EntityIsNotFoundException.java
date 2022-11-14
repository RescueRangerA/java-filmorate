package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

public class EntityIsNotFoundException extends Exception {
    public EntityIsNotFoundException(Entity entity) {
        super(String.format("Entity '%s' with id '%d' is not found", entity.getClass(), entity.getId()));
    }

    public <T extends Entity> EntityIsNotFoundException(Class<T> entityClass, Long entityId) {
        super(String.format("Entity '%s' with id '%d' is not found", entityClass.getName(), entityId));
    }
}
