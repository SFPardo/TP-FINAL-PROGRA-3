package com.example.demo.validation.annotation;

import com.example.demo.validation.validator.EnumValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
public @interface EnumValue {
    Class<? extends Enum<?>> enumClass();
    String message() default "Valor no válido para el tipo de cuenta.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}