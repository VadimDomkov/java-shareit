package ru.practicum.shareit.exceptions;

public class ValueIsNotEnumException extends RuntimeException {
    public ValueIsNotEnumException(String message) {
        super(message);
    }
}
