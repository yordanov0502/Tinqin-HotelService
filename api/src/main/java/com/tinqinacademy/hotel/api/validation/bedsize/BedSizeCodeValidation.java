package com.tinqinacademy.hotel.api.validation.bedsize;

import com.tinqinacademy.hotel.api.model.enums.BedSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class BedSizeCodeValidation implements ConstraintValidator<BedSizeCode, BedSize> {
    @Override
    public void initialize(BedSizeCode constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BedSize bedSize, ConstraintValidatorContext context) {
        return !BedSize.UNKNOWN.equals(bedSize);
    }
}
