package com.tinqinacademy.hotel.api.validation.bathroomtype;

import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class BathroomTypeCodeValidation implements ConstraintValidator<BathroomTypeCode,BathroomType> {
    @Override
    public void initialize(BathroomTypeCode constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BathroomType bathroomType, ConstraintValidatorContext context) {
         return !BathroomType.UNKNOWN.equals(bathroomType);
    }
}
