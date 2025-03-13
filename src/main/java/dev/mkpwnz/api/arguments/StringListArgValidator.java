package dev.mkpwnz.api.arguments;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class StringListArgValidator extends ArgumentValidator<String> {
    private final List<String> allowedValues;
    private final boolean caseSensitive;

    public StringListArgValidator(String name, String description, boolean required, String[] allowedValues, boolean caseSensitive) {
        super(name, description, required);
        this.allowedValues = Arrays.asList(allowedValues);
        this.caseSensitive = caseSensitive;
    }

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

    @Override
    public List<String> getTabCompletions(CommandSender sender) {
        return allowedValues;
    }
}