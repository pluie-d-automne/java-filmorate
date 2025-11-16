package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoWhitespacesValidator implements ConstraintValidator<NoWhitespaces, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            for (char ch : value.toCharArray()) {
                if (Character.isWhitespace(ch)) {
                    return false;
                }
            }
        }
        return true;
    }
}
