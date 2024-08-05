package com.tinqinacademy.hotel.api.exceptions.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends CustomException{

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {super(message);}
}