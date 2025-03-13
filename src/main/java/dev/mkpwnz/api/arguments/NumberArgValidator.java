package dev.mkpwnz.api.arguments;


import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class NumberArgValidator extends ArgumentValidator<Number> {
    private final double min;
    private final double max;

    public NumberArgValidator(String name, String description, boolean required, double min, double max) {
        super(name, description, required);
        this.min = min;
        this.max = max;
    }

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

    @Override
    public List<String> getTabCompletions(CommandSender sender) {
        return new ArrayList<>();
    }
}
