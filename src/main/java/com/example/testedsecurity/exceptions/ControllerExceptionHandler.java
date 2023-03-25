package com.example.testedsecurity.exceptions;

import com.example.testedsecurity.dtos.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = UserRegistrationException.class)
    public ResponseEntity<ErrorDto> registrationHandler(UserRegistrationException exception) {
        return new ResponseEntity<>(new ErrorDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BookAlreadyExistsException.class, BookNotFoundException.class})
    public ResponseEntity<ErrorDto> bookHandler(BookAlreadyExistsException exception) {
        return new ResponseEntity<>(new ErrorDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
