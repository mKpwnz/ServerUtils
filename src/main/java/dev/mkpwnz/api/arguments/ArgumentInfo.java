package dev.mkpwnz.api.arguments;

/**
 * Represents metadata for a single argument, including its name, description,
 * and whether it is required.
 *
 * @param name        The name of the argument. This is used to identify the argument.
 * @param description A brief description of the argument. Can provide additional context or details.
 * @param required    Specifies whether the argument is mandatory. If true, the argument
 *                    must be provided; otherwise, it is optional.
 */
public record ArgumentInfo(String name, String description, boolean required) {
}

