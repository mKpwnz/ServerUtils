package dev.mkpwnz.api.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringListArg {
    String name();

    String description() default "";

    boolean required() default true;

    String[] allowedValues();

    boolean caseSensitive() default false;
}
