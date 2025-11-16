package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = NoWhitespacesValidator.class)
@Documented
public @interface NoWhitespaces {

    String message() default "{NoWhitespacesValidator.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}

