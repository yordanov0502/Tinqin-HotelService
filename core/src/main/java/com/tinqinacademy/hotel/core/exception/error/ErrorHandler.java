package com.tinqinacademy.hotel.core.exception.error;

import com.tinqinacademy.hotel.api.error.Error;
import com.tinqinacademy.hotel.core.exception.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Service
public class ErrorHandler implements ErrorService{
    @Override
    public Error handle(Throwable throwable) {
        return Match(throwable).of(

                Case($(instanceOf(NotFoundException.class)),
                        ex -> Error.builder()
                                .errMsg(ex.getMessage())
                                .httpStatus(ex.getHttpStatus())
                                .build()),

                Case($(instanceOf(DuplicateValueException.class)),
                        ex -> Error.builder()
                                .errMsg(ex.getMessage())
                                .httpStatus(ex.getHttpStatus())
                                .build()),

                Case($(instanceOf(BookingDatesException.class)),
                        ex -> Error.builder()
                                .errMsg(ex.getMessage())
                                .httpStatus(ex.getHttpStatus())
                                .build()),

                Case($(instanceOf(BookedRoomException.class)),
                        ex -> Error.builder()
                                .errMsg(ex.getMessage())
                                .httpStatus(ex.getHttpStatus())
                                .build()),

                Case($(instanceOf(UnsupportedOperationException.class)),
                        ex -> Error.builder()
                                .errMsg(ex.getMessage())
                                .httpStatus(HttpStatus.NOT_IMPLEMENTED)
                                .build()),

                Case($(instanceOf(NoMethodFoundException.class)),
                        ex -> Error.builder()
                                .errMsg(ex.getMessage())
                                .httpStatus(ex.getHttpStatus())
                                .build()),

                Case($(),
                        ex -> Error.builder()
                                .errMsg(ex.getMessage())
                                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build())

        );
    }
}
