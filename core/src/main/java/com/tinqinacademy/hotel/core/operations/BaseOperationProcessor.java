package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.core.exception.error.ExceptionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public abstract class BaseOperationProcessor{

    protected final ConversionService conversionService;
    protected final ExceptionService exceptionService;
    private final Validator validator;

    protected <T extends OperationInput> void validate(T input){
        Set<ConstraintViolation<T>> violationSet = validator.validate(input);
        if(!violationSet.isEmpty()){
            throw new ConstraintViolationException(violationSet);
        }
    }
}