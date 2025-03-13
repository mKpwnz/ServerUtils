package dev.mkpwnz.api.arguments;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * A validator for boolean command arguments. This class checks whether
 * the input corresponds to predefined true or false values and provides
 * appropriate validation results. Additionally, it supports tab completion
 * with suggested options.
 */
public class BoolArgValidator extends ArgumentValidator<Boolean> {
    private final List<String> trueValues;
    private final List<String> falseValues;

    /**
     * Constructs a BoolArgValidator instance, which validates boolean arguments
     * by checking if the input matches predefined true or false values.
     *
     * @param name        The name of the argument being validated.
     * @param description A brief description of the purpose or function of the argument.
     * @param required    Specifies whether the argument is mandatory. If true, the argument must be supplied.
     * @param trueValues  An array of string values that are considered as indicating "true".
     * @param falseValues An array of string values that are considered as indicating "false".
     */
    public BoolArgValidator(String name, String description, boolean required,
                            String[] trueValues, String[] falseValues) {
        super(name, description, required);
        this.trueValues = Arrays.asList(trueValues);
        this.falseValues = Arrays.asList(falseValues);
    }

    /**
     * Validates the provided input string to determine whether it matches predefined true or false values.
     * Returns a ValidationResult indicating success or an error message if validation fails.
     *
     * @param input  The input string to be validated. It may represent a true or false value
     *               from the predefined valid values.
     * @param sender The CommandSender instance related to the validation. This parameter
     *               provides context about who or what is invoking the validation process.
     *
     * @return A ValidationResult containing either the boolean value (true or false) upon successful
     * validation, or an error message if the input is invalid.
     */
    @Override
    public ValidationResult<Boolean> validate(String input, CommandSender sender) {
        if (input == null || input.trim().isEmpty()) {
            return ValidationResult.error(
                    this.getName() + " benötigt einen gültigen Wert!");
        }

        String lowercaseInput = input.toLowerCase();

        if (trueValues.contains(lowercaseInput)) {
            return ValidationResult.success(true);
        }

        if (falseValues.contains(lowercaseInput)) {
            return ValidationResult.success(false);
        }

        return ValidationResult.error(
                this.getName() + " muss einer der folgenden Werte sein: " +
                        String.join(", ", trueValues) + " oder " +
                        String.join(", ", falseValues));
    }

    /**
     * Provides a list of possible tab completions for the current context.
     * This method is used to suggest predefined "true" or "false" values
     * when a command sender is typing a command that expects a boolean argument.
     *
     * @param sender The command sender requesting tab completion. This may represent
     *               a player, console, or other entity executing the command.
     *
     * @return A list of string values representing the first entries of predefined
     * "true" and "false" values for tab completion suggestions.
     */
    @Override
    public List<String> getTabCompletions(CommandSender sender) {
        return Arrays.asList(trueValues.getFirst(), falseValues.getFirst());
    }
}