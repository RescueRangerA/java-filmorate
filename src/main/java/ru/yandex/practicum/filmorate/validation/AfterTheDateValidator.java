package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.validation.constraints.AfterTheDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AfterTheDateValidator implements ConstraintValidator<AfterTheDate, LocalDate> {

    AfterTheDate constraintAnnotation;

    @Override
    public void initialize(AfterTheDate constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(LocalDate given, ConstraintValidatorContext constraintValidatorContext) {
        if (given == null) {
            return true;
        }

        LocalDate since = LocalDate.parse(this.constraintAnnotation.moment(), DateTimeFormatter.ISO_DATE);

        return given.isAfter(since);
    }
}
