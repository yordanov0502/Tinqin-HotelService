package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.exceptions.Errors;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public abstract class BaseController {

    protected <O extends OperationOutput> ResponseEntity<?> mapToResponseEntity(Either<Errors, O> either, HttpStatus httpStatus) {

        return either.isRight()
                ? new ResponseEntity<>(either.get(), httpStatus)
                : new ResponseEntity<>(either.getLeft().getErrorList(), either.getLeft().getHttpStatus());
    }
}
