package dev.mkpwnz.api.arguments;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * A specialized implementation of {@code ArgumentValidator} that validates numeric
 * command arguments. This validator ensures the provided numeric value is within
 * the specified range and can be parsed correctly as a {@code Number}.
 */
public class NumberArgValidator extends ArgumentValidator<Number> {
    private final double min;
    private final double max;

    /**
     * Constructs a NumberArgValidator to validate numeric arguments within a specified range.
     *
     * @param name        The name of the argument. This is used to identify the argument in commands.
     * @param description A brief description of the argument, providing details about its purpose or usage.
     * @param required    Specifies whether the argument is mandatory. If true, the argument must be provided.
     * @param min         The minimum acceptable value for the argument.
     * @param max         The maximum acceptable value for the argument.
     */
    public NumberArgValidator(String name, String description, boolean required, double min, double max) {
        super(name, description, required);
        this.min = min;
        this.max = max;
    }

    /**
     * Validates a numeric input provided as a string. Ensures that the input can be
     * parsed to a number and checks whether the number falls within the specified range.
     *
     * @param input  The string input to validate. This should be a numeric value as a string.
     * @param sender The command sender. Provides context or additional information about
     *               the source of the input.
     *
     * @return A {@code ValidationResult<Number>} object that represents the result of the
     * validation. It contains the parsed number if validation is successful or
     * an appropriate error message if the validation fails.
     */
    @Override
    public ValidationResult<Number> validate(String input, CommandSender sender) {
        try {
            double value = Double.parseDouble(input);
            if (value < min) {
                return ValidationResult.error(this.getName() + " muss mindestens " + min + " sein!");
            }
            if (value > max) {
                return ValidationResult.error(this.getName() + " darf maximal " + max + " sein!");
            }
            return ValidationResult.success(value);
        } catch (NumberFormatException e) {
            return ValidationResult.error(this.getName() + " muss eine gültige Zahl sein!");
        }
    }

    /**
     * Provides a list of suggestions or completions for a command.
     * This method is used to assist users by offering possible completions
     * for the current argument being typed in a command.
     *
     * @param sender The command sender requesting tab completions. This provides
     *               context about who or what is making the request (e.g., a player
     *               or the console).
     *
     * @return A list of possible completions. If no suggestions are available,
     * an empty list is returned.
     */
    @Override
    public List<String> getTabCompletions(CommandSender sender) {
        return new ArrayList<>();
    }
}
