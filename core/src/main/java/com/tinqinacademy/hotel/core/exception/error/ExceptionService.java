package com.tinqinacademy.hotel.core.exception.error;

import com.tinqinacademy.hotel.api.error.Errors;

public interface ExceptionService {
    Errors handle(Throwable throwable);
}
