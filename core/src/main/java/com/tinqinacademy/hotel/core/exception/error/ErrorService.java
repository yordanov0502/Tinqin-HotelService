package com.tinqinacademy.hotel.core.exception.error;

import com.tinqinacademy.hotel.api.error.Error;

public interface ErrorService {
    Error handle(Throwable throwable);
}
