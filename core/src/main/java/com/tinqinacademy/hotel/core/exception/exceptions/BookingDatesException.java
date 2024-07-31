package com.tinqinacademy.hotel.core.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BookingDatesException extends CustomException{

    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public BookingDatesException(String message) {super(message);}
}