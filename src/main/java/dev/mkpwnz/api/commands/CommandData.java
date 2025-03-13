package dev.mkpwnz.api.commands;

import dev.mkpwnz.api.arguments.ArgumentInfo;

import java.util.List;

public record CommandData(String fullName, String description, String permission, List<ArgumentInfo> parameters) {

    public String getParametersAsString() {
        StringBuilder result = new StringBuilder();
        for (ArgumentInfo param : parameters) {
            if (!result.isEmpty()) {
                result.append(" ");
            }
            result.append(param.required() ? "<" : "[").append(param.name()).append(param.required() ? ">" : "]");
        }
        return result.toString();
    }
}

