package dev.mkpwnz.api.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify metadata for string parameters used in commands
 * or similar functionalities. This annotation provides options for name,
 * description, length constraints, and requirement status.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringArg {
    String name();

    String description() default "";

    int minLength() default 0;

    int maxLength() default Integer.MAX_VALUE;

    boolean required() default true;
}
