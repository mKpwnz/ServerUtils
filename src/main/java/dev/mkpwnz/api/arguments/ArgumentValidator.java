package dev.mkpwnz.api.arguments;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Abstract base class for validating command arguments of type {@code T}.
 * It provides a framework for verifying raw input, returning parsed or validated results,
 * and generating tab completions for command interfaces.
 *
 * @param <T> The type of the value that this validator will handle
 *            upon successful validation of the input.
 */
public abstract class ArgumentValidator<T> {
    private final String name;
    private final String description;
    private final boolean required;

    /**
     * Constructs an ArgumentValidator with the specified name, description, and required status.
     *
     * @param name        The name of the argument. This is used to identify the argument in commands.
     * @param description A brief description of the argument, providing details about its purpose or usage.
     * @param required    Specifies whether the argument is mandatory. If true, the argument must be provided.
     */
    public ArgumentValidator(String name, String description, boolean required) {
        this.name = name;
        this.description = description;
        this.required = required;
    }

    /**
     * Validates the provided input and returns a {@link ValidationResult} indicating whether
     * the validation was successful or not, along with additional details such as the parsed value
     * or an error message.
     *
     * @param input  The raw input string to be validated. It represents the value to be checked
     *               for compliance with the validation rules defined by the concrete implementation.
     * @param sender The {@link CommandSender} that issued the command.
     *               This can be used to customize validation behavior based on the sender.
     * @return A {@link ValidationResult} object containing the validation status, parsed value
     * if successful, or an error message if the validation fails.
     */
    public abstract ValidationResult<T> validate(String input, CommandSender sender);

    /**
     * Provides a list of possible tab completions for a command argument
     * based on the context of the invoking {@link CommandSender}.
     * This method is designed to assist in the generation of
     * auto-completion suggestions in a command interface.
     *
     * @param sender The {@link CommandSender} that issued the command, representing
     *               the source of the command execution. This parameter can be used to
     *               customize the tab completion options, e.g., based on the sender's
     *               permissions or current context.
     * @return A {@link List} of {@link String} containing possible tab completion
     * suggestions. The returned list may be empty if no suggestions are available.
     */
    public abstract List<String> getTabCompletions(CommandSender sender);

    /**
     * Retrieves the name of the argument.
     *
     * @return A string representing the name of the argument.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the description of the argument.
     *
     * @return A string representing the description of the argument.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Determines whether the argument is required.
     *
     * @return true if the argument is required; false otherwise.
     */
    public boolean isRequired() {
        return required;
    }
}

