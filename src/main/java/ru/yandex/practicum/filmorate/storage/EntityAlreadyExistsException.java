package ru.yandex.practicum.filmorate.storage;

public class EntityAlreadyExistsException extends Exception {
    public EntityAlreadyExistsException(Object entity) {
        super(String.format("Entity '%s' with props '%s' already exists", entity.getClass(), entity));
    }
}
