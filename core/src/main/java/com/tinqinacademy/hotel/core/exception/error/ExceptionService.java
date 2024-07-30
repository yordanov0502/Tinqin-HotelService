package com.tinqinacademy.hotel.core.exception.error;

import com.tinqinacademy.hotel.api.error.Error;

public interface ExceptionService {
    Error handle(Throwable throwable);
}
