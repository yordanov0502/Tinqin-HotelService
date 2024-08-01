package com.tinqinacademy.hotel.core.exceptions.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BookedRoomException extends CustomException{

    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public BookedRoomException(String message) {super(message);}
}