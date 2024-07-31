package com.tinqinacademy.hotel.core.exceptions;

import com.tinqinacademy.hotel.api.error.Errors;

public interface ExceptionService {
    Errors handle(Throwable throwable);
}
