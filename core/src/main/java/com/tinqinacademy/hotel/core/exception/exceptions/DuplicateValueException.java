package com.tinqinacademy.hotel.core.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateValueException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public DuplicateValueException(String message) {super(message);}
}