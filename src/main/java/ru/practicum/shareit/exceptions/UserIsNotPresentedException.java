package ru.practicum.shareit.exceptions;

public class UserIsNotPresentedException extends RuntimeException {
    public UserIsNotPresentedException(String message) {
        super(message);
    }
}
