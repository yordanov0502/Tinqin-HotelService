package com.tinqinacademy.hotel.api.exceptions.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateValueException extends CustomException{

    private final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public DuplicateValueException(String message) {super(message);}
}