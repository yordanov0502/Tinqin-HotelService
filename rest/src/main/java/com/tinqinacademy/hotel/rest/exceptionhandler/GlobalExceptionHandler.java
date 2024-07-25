package com.tinqinacademy.hotel.rest.exceptionhandler;

import com.tinqinacademy.hotel.core.exception.ErrorService;
import com.tinqinacademy.hotel.core.exception.ErrorsWrapper;
import com.tinqinacademy.hotel.core.exception.exceptions.BookedRoomException;
import com.tinqinacademy.hotel.core.exception.exceptions.BookingDatesException;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorService errorService;


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){

        ErrorsWrapper errorsWrapper = errorService.handle(exception);

        return new ResponseEntity<>(errorsWrapper.getErrorList(),errorsWrapper.getHttpStatus());
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(NotFoundException exception){

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BookedRoomException.class})
    public ResponseEntity<?> handleBookedRoomException(BookedRoomException exception){

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BookingDatesException.class})
    public ResponseEntity<?> handleBookingDatesException(BookingDatesException exception){

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnsupportedOperationException.class})
    public ResponseEntity<?> handleUnsupportedOperationException(UnsupportedOperationException exception){

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }
}
