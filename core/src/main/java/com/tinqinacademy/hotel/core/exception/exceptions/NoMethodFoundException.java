package com.tinqinacademy.hotel.core.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoMethodFoundException extends RuntimeException{

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public NoMethodFoundException(String message) {super(message);}
}