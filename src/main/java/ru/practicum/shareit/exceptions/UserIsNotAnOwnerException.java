package ru.practicum.shareit.exceptions;

public class UserIsNotAnOwnerException extends RuntimeException {
    public UserIsNotAnOwnerException(String message) {
        super(message);
    }
}
