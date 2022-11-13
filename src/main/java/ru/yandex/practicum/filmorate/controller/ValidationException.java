package ru.yandex.practicum.filmorate.controller;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
