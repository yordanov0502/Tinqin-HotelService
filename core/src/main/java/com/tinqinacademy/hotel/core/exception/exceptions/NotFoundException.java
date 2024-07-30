package com.tinqinacademy.hotel.core.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException{

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {super(message);}
}