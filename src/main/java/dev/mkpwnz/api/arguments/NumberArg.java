package dev.mkpwnz.api.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify metadata for numeric parameters used in commands or similar functionalities.
 * This annotation defines constraints such as minimum, maximum values, and whether the parameter is required.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberArg {
    String name();

    String description() default "";

    double min() default Double.MIN_VALUE;

    double max() default Double.MAX_VALUE;

    boolean required() default true;
}
