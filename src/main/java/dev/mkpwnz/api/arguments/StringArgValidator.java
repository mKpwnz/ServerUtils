package dev.mkpwnz.api.arguments;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class StringArgValidator extends ArgumentValidator<String> {
    private final int minLength;
    private final int maxLength;

    public StringArgValidator(String name, String description, boolean required,
                              int minLength, int maxLength) {
        super(name, description, required);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

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

    @Override
    public List<String> getTabCompletions(CommandSender sender) {
        return new ArrayList<>();
    }
}