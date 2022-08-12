package ru.yandex.practicum.filmorate.exception;

public class IncorrectUserIdException extends RuntimeException{

    public IncorrectUserIdException(String message){
        super(message);
    }
}
