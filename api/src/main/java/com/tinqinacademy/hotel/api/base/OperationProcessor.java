package com.tinqinacademy.hotel.api.base;

import com.tinqinacademy.hotel.api.error.Error;
import io.vavr.control.Either;

public interface OperationProcessor<I extends  OperationInput, O extends OperationOutput > {
    Either<Error,O> process(I input);
}
