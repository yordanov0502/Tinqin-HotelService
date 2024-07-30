package com.tinqinacademy.hotel.core.exception.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BookedRoomException extends RuntimeException{

    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public BookedRoomException(String message) {super(message);}
}