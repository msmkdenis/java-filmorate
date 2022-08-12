package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorHandler {

    /*
    Статусы ошибок:
    400 — если ошибка валидации: ValidationException (Bad request)
    404 — для всех ситуаций, если искомый объект не найден (Not Found)
    500 — если возникло внутреннее исключение (Internal Server Error)
     */

    @ExceptionHandler
    public ResponseEntity<String> handleUserNotFoundException(final IncorrectUserIdException e) {
        return new ResponseEntity<>( e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleFilmNotFoundException(final IncorrectFilmIdException e) {
        return new ResponseEntity<> (e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleMpaNotFoundException(final IncorrectMpaIdException e) {
        return new ResponseEntity<> (e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleGenreNotFoundException(final IncorrectGenreIdException e) {
        return new ResponseEntity<> (e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUserAlreadyExistException(final UserAlreadyExistsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> constraintViolation(final ConstraintViolationException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> argumentViolation(final MethodArgumentNotValidException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}