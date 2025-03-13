package dev.mkpwnz.api.arguments;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * A concrete implementation of {@link ArgumentValidator} to validate string arguments
 * with specific length constraints.
 * This validator ensures that the input string meets the defined minimum and maximum
 * length requirements, and returns appropriate validation results.
 */
public class StringArgValidator extends ArgumentValidator<String> {
    private final int minLength;
    private final int maxLength;

    /**
     * Constructs a StringArgValidator with the specified parameters.
     * This validator is used to validate string arguments with specific
     * length constraints, determining if the input string is of an appropriate
     * length as defined by the minimum and maximum limits.
     *
     * @param name        The name of the argument. This is used to identify the argument.
     * @param description A brief description of the argument, explaining its purpose or usage.
     * @param required    Specifies whether the argument is mandatory. If true, the argument must be provided.
     * @param minLength   The minimum length for the string argument. Validation will fail if the input is shorter.
     * @param maxLength   The maximum length for the string argument. Validation will fail if the input exceeds this size.
     */
    public StringArgValidator(String name, String description, boolean required,
                              int minLength, int maxLength) {
        super(name, description, required);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    /**
     * Validates the provided string input based on specified length constraints.
     * If the input string fails to meet the constraints, an error result is returned
     * with an appropriate error message. Otherwise, the input is considered valid.
     *
     * @param input  The string input to be validated.
     * @param sender The command sender, representing the source of the command or action.
     *
     * @return A ValidationResult<String> containing either a success with the validated
     * input if it meets the length constraints, or an error with a descriptive
     * message if the input is invalid.
     */
    @Override
    public ValidationResult<String> validate(String input, CommandSender sender) {
        if (input.length() < minLength) {
            return ValidationResult.error(
                    this.getName() + " muss mindestens " + minLength + " Zeichen lang sein!");
        }
        if (input.length() > maxLength) {
            return ValidationResult.error(
                    this.getName() + " darf maximal " + maxLength + " Zeichen lang sein!");
        }
        return ValidationResult.success(input);
    }

    /**
     * Retrieves a list of suggestions for tab completions based on the provided sender context.
     * This is typically used in command-related functionalities to assist the user in
     * completing their input.
     *
     * @param sender The command sender, representing the source of the command,
     *               such as a player, console, or other type of executor.
     *
     * @return A list of strings containing possible tab completion options.
     * If no options are available, an empty list is returned.
     */
    @Override
    public List<String> getTabCompletions(CommandSender sender) {
        return new ArrayList<>();
    }
}