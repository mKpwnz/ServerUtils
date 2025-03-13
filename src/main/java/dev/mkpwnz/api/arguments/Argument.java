package dev.mkpwnz.api.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies metadata for a method parameter used as an argument in commands or similar functionalities.
 * The annotation tracks the name, description, and requirement status of the parameter.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {
    String name();

    String description() default "";

    boolean required() default true;
}


