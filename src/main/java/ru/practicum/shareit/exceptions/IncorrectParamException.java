package ru.practicum.shareit.exceptions;

public class IncorrectParamException extends RuntimeException{
    public IncorrectParamException(String message) {
        super(message);
    }
}
