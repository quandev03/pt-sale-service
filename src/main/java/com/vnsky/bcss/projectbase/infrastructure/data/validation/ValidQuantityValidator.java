package com.vnsky.bcss.projectbase.infrastructure.data.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidQuantityValidator implements ConstraintValidator<ValidQuantity, Object> {

    @Override
    public void initialize(ValidQuantity constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // If value is null, it's invalid (will be handled by @NotNull)
        if (value == null) {
            return false;
        }

        // If value is not an Integer, it's invalid
        if (!(value instanceof Integer quantity)) {
            return false;
        }

        // Check if quantity is positive
        return quantity > 0;
    }
}
