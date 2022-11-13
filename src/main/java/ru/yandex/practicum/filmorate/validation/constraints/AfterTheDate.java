package ru.yandex.practicum.filmorate.validation.constraints;

import ru.yandex.practicum.filmorate.validation.AfterTheDateValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(AfterTheDate.List.class)
@Documented
@Constraint(validatedBy = {AfterTheDateValidator.class})
public @interface AfterTheDate {

    String message() default "{ru.yandex.practicum.filmorate.validation.constraints.After.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String moment();

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        AfterTheDate[] value();
    }
}
