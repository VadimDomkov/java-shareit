package ru.practicum.shareit.exceptions;

public class EmailBelongsToOtherUserException extends RuntimeException {
    public EmailBelongsToOtherUserException(String message) {
        super(message);
    }
}
