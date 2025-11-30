package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CinemaDateValidator implements ConstraintValidator<CinemaDate, LocalDate> {
    private static final LocalDate MIN_CINEMA_DATE = LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_DATE);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date != null) {
            return date.isAfter(MIN_CINEMA_DATE);
        }
        return true;
    }

}
