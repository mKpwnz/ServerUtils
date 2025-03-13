package dev.mkpwnz.api.arguments;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;


/**
 * A validator implementation for validating string arguments against a predefined list of allowed values.
 * This validator ensures that the provided input matches one of the allowed values. The validation process
 * can be either case-sensitive or case-insensitive, as defined during initialization.
 */
public class StringListArgValidator extends ArgumentValidator<String> {
    private final List<String> allowedValues;
    private final boolean caseSensitive;

    /**
     * Constructs a StringListArgValidator with the specified name, description, required status,
     * allowed values, and case sensitivity. This validator checks whether the input string
     * matches one of the allowed values, taking case sensitivity into account as specified.
     *
     * @param name          The name of the argument. This is used to identify the argument in commands.
     * @param description   A brief description of the argument, providing details about its purpose or usage.
     * @param required      Specifies whether the argument is mandatory. If true, the argument must be provided.
     * @param allowedValues An array of allowed string values for validation. The input string must match one of these values.
     * @param caseSensitive Determines if the validation should be case-sensitive. If true, the string comparison is case-sensitive.
     */
    public StringListArgValidator(String name, String description, boolean required, String[] allowedValues, boolean caseSensitive) {
        super(name, description, required);
        this.allowedValues = Arrays.asList(allowedValues);
        this.caseSensitive = caseSensitive;
    }

    /**
     * Validates a given string input against a predefined list of allowed values.
     * This validation is performed either case-sensitively or case-insensitively,
     * based on the initialization configuration of the validator.
     *
     * @param input  The string input to be validated. Must not be null or empty.
     * @param sender The command sender attempting to provide the input.
     *               This may contain contextual information about the command execution environment.
     *
     * @return A {@link ValidationResult} object representing the result of the validation process.
     * If validation is successful, the result contains the validated value.
     * Otherwise, it contains an error message detailing why validation failed.
     */
    @Override
    public ValidationResult<String> validate(String input, CommandSender sender) {
        if (input == null || input.isEmpty()) {
            return ValidationResult.error(
                    this.getName() + " ist erforderlich!");
        }

        String compareValue = this.caseSensitive ? input : input.toLowerCase();
        List<String> compareAllowed = allowedValues.stream()
                .map(v -> this.caseSensitive ? v : v.toLowerCase())
                .toList();

        if (!compareAllowed.contains(compareValue)) {
            return ValidationResult.error(
                    String.format("'%s' ist kein gültiger Wert für '%s'. Erlaubte Werte: %s",
                            input,
                            this.getName(),
                            String.join(", ", this.allowedValues)
                    )
            );
        }

        if (this.caseSensitive) {
            return ValidationResult.success(input);
        } else {
            int index = compareAllowed.indexOf(compareValue);
            return ValidationResult.success(this.allowedValues.get(index));
        }
    }

    /**
     * Provides a list of potential tab completions for a command input based on
     * the preconfigured allowed values. This method is typically used to
     * enhance user experience during command input by providing suggestions.
     *
     * @param sender The sender of the command, which may provide additional
     *               context for generating the suggestions. For example, this
     *               may represent the player or system entity issuing the
     *               command.
     *
     * @return A list of string suggestions for tab completion. The returned list
     * contains values that are allowed based on the predefined configuration.
     */
    @Override
    public List<String> getTabCompletions(CommandSender sender) {
        return allowedValues;
    }
}