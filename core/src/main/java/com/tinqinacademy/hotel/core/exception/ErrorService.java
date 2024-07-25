package com.tinqinacademy.hotel.core.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;

public interface ErrorService {
    ErrorsWrapper handle(MethodArgumentNotValidException exception);
}
