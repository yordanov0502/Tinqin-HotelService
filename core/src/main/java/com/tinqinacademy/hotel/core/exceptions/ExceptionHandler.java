package com.tinqinacademy.hotel.core.exceptions;

import com.tinqinacademy.hotel.api.error.Error;
import com.tinqinacademy.hotel.api.error.Errors;
import com.tinqinacademy.hotel.core.exceptions.custom.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Service
public class ExceptionHandler implements ExceptionService {
    @Override
    public Errors handle(Throwable throwable) {
        return Match(throwable).of(
                Case($(instanceOf(CustomException.class)), this::handleCustomException),
                Case($(instanceOf(ConstraintViolationException.class)), this::handleConstraintViolationException),
                Case($(), this::handleDefaultException)
        );
    }

    private Errors handleCustomException(CustomException ex) {
        return Errors.builder()
                .errorList(List.of(Error.builder().errMsg(ex.getMessage()).build()))
                .httpStatus(ex.getHttpStatus())
                .build();
    }

    private Errors handleConstraintViolationException(ConstraintViolationException ex) {
        List<Error> errorList = ex.getConstraintViolations()
                .stream()
                .map(violation -> Error.builder()
                        .field(violation.getPropertyPath().toString())
                        .errMsg(violation.getMessage())
                        .build())
                .collect(Collectors.toList());

        return Errors.builder()
                .errorList(errorList)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    private Errors handleDefaultException(Throwable throwable) {
        return Match(throwable).of(

                Case($(instanceOf(UnsupportedOperationException.class)),
                        ex -> Errors.builder()
                                .errorList(List.of(Error.builder().errMsg(ex.getMessage()).build()))
                                .httpStatus(HttpStatus.NOT_IMPLEMENTED)
                                .build()),

                Case($(),
                        ex -> Errors.builder()
                                .errorList(List.of(Error.builder().errMsg(ex.getMessage()).build()))
                                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build())
        );

    }

}
