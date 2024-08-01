package com.tinqinacademy.hotel.core.exceptions;

import com.tinqinacademy.hotel.api.error.Error;
import com.tinqinacademy.hotel.api.error.Errors;
import com.tinqinacademy.hotel.core.exceptions.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Component
public class ExceptionHandler implements ExceptionService {
    @Override
    public Errors handle(Throwable throwable) {
        return Match(throwable).of(
                Case($(instanceOf(CustomException.class)), this::handleCustomException),
                Case($(instanceOf(ViolationsException.class)), this::handleViolationsException),
                Case($(), this::handleDefaultException)
        );
    }

    private Errors handleCustomException(CustomException ex) {
        return Errors.builder()
                .errorList(List.of(Error.builder().errMsg(ex.getMessage()).build()))
                .httpStatus(ex.getHttpStatus())
                .build();
    }

    private Errors handleViolationsException(ViolationsException ex) {
        return Errors.builder()
                .errorList(ex.getErrorList())
                .httpStatus(ex.getHttpStatus())
                .build();
    }

    private Errors handleDefaultException(Throwable throwable) {
        return Match(throwable).of(

                Case($(instanceOf(UnsupportedOperationException.class)),
                        ex -> Errors.builder()
                                .errorList(List.of(Error.builder().errMsg(ex.getMessage()).build()))
                                .httpStatus(HttpStatus.BAD_REQUEST) //! NOT_IMPLEMENTED
                                .build()),

                Case($(),
                        ex -> Errors.builder()
                                .errorList(List.of(Error.builder().errMsg(ex.getMessage()).build()))
                                .httpStatus(HttpStatus.BAD_REQUEST) //! INTERNAL_SERVER_ERROR
                                .build())
        );

    }

}
