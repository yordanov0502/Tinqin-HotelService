package com.tinqinacademy.hotel.api.exceptions.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnbookException extends CustomException{

    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public UnbookException(String message) {super(message);}
}
