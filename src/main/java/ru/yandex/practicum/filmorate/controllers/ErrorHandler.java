package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ProblematicLikesException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(final NotFoundException e) {
        log.info("Возникла ошибка 404: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleProblematicLikesException(final ProblematicLikesException e) {
        log.info("Возникла ошибка 409: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        log.info("Возникла непредвиденная ошибка {}", e.getMessage());
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