package com.tinqinacademy.hotel.core.exceptions.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoMethodFoundException extends CustomException{

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public NoMethodFoundException(String message) {super(message);}
}