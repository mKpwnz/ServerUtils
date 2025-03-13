package dev.mkpwnz.api.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify metadata for player-related parameters used in commands
 * or similar functionalities. This annotation provides additional options for
 * validating player arguments such as whether the player should be online and
 * whether the argument is mandatory.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlayerArg {
    String name();

    String description() default "";

    boolean onlineOnly() default true;

    boolean required() default true;
}



