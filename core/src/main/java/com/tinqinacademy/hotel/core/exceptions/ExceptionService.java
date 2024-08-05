package com.tinqinacademy.hotel.core.exceptions;

import com.tinqinacademy.hotel.api.exceptions.Errors;

public interface ExceptionService {
    Errors handle(Throwable throwable);
}
