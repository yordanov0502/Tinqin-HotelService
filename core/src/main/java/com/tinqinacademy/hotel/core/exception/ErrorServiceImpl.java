package com.tinqinacademy.hotel.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

@Component
public class ErrorServiceImpl implements ErrorService{


    public ErrorsWrapper handle(MethodArgumentNotValidException exception){

        List<ErrorInfo> errors = new ArrayList<>();
        exception.getBindingResult()
                 .getFieldErrors()
                 .forEach(error ->
                         errors.add(ErrorInfo.builder()
                                 .field(error.getField())
                                 .exceptionMessage(error.getDefaultMessage())
                                 .build()));

        ErrorsWrapper errorsWrapper = ErrorsWrapper.builder()
                .errorList(errors)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();

       return errorsWrapper;
    }

}
