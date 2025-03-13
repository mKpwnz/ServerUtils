package dev.mkpwnz.api.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define metadata for a command. This annotation can be
 * applied to methods to configure their command-related attributes such as name,
 * hierarchy, description, permission requirements, and usage information.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();

    String[] parent() default {};

    String description() default "";

    String permission() default "";

    String usage() default "";
}

