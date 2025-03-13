package dev.mkpwnz.api.arguments;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class ArgumentValidator<T> {
    private final String name;
    private final String description;
    private final boolean required;

    public ArgumentValidator(String name, String description, boolean required) {
        this.name = name;
        this.description = description;
        this.required = required;
    }

    public abstract ValidationResult<T> validate(String input, CommandSender sender);

    public abstract List<String> getTabCompletions(CommandSender sender);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }
}

