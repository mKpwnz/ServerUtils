package dev.mkpwnz.api.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify metadata for a parameter of type list of strings used
 * in commands or similar functionalities. This annotation defines options for
 * parameter validation such as allowed values, case sensitivity, and whether
 * the parameter is required.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringListArg {
    String name();

    String description() default "";

    boolean required() default true;

    String[] allowedValues();

    boolean caseSensitive() default false;
}
