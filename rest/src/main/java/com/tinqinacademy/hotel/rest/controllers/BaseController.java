package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.error.Errors;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public abstract class BaseController {

    protected <O extends OperationOutput> ResponseEntity<?> mapToResponseEntity(Either<Errors, O> either, HttpStatus httpStatus) {

        log.info(String.format("Start %s %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName()));

        ResponseEntity<?> responseEntity;

        if(either.isRight()) {
            responseEntity = new ResponseEntity<>(either.get(), httpStatus);
        } else {
            responseEntity = new ResponseEntity<>(either.getLeft().getErrorList(), either.getLeft().getHttpStatus());
        }

        log.info(String.format("End %s %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName()));

        return responseEntity;
    }
}
