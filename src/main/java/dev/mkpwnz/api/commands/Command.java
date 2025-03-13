package dev.mkpwnz.api.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a command and its metadata in a command system. The annotation
 * is applied to methods to mark them as command handlers. The associated metadata includes
 * attributes such as the name of the command, its parent commands, description, permissions,
 * and usage information.
 * <p>
 * Methods annotated with {@code @Command} are expected to handle logic for executing the command,
 * validating permissions, managing arguments, and providing usage information where applicable.
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

