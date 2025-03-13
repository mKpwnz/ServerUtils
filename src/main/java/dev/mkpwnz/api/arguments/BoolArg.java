package dev.mkpwnz.api.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify metadata for boolean parameters used in commands or
 * similar functionalities. This annotation provides options for defining
 * the parameter name, description, requirement status, and valid representations
 * of true/false values.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BoolArg {
    String name();

    String description() default "";

    boolean required() default true;

    String[] trueValues() default {"true", "1"};

    String[] falseValues() default {"false", "0"};
}