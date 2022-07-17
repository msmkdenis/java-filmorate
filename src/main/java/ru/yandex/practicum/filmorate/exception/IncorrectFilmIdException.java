package ru.yandex.practicum.filmorate.exception;

public class IncorrectFilmIdException extends RuntimeException{

    public IncorrectFilmIdException(String message) {
        super(message);
    }
}
