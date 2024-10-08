package com.tinqinacademy.hotel.api.validation.name;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({FIELD,TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = NameRegexValidation.class)
public @interface NameRegex {
    String message() default "Invalid type of name.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
