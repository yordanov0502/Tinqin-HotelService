package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.exceptions.Error;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.api.exceptions.custom.ViolationsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public abstract class BaseOperationProcessor{

    protected final ConversionService conversionService;
    protected final ExceptionService exceptionService;
    private final Validator validator;

    protected <I extends OperationInput> void validate(I input){
        Set<ConstraintViolation<I>> violationSet = validator.validate(input);

        if(!violationSet.isEmpty()){
        List<Error> errorList = violationSet
                .stream()
                .map(violation -> Error.builder()
                        .field(violation.getPropertyPath().toString())
                        .errMsg(violation.getMessage())
                        .build())
                .collect(Collectors.toList());
        throw new ViolationsException("Violation exception occurred.",errorList);
        }
    }
}