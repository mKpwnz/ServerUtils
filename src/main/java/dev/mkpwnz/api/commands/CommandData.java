package dev.mkpwnz.api.commands;

import dev.mkpwnz.api.arguments.ArgumentInfo;

import java.util.List;

/**
 * Represents metadata for a command, including its name, description, permission
 * requirements, and a list of arguments.
 *
 * @param fullName    The full name of the command. Typically used to identify the command.
 * @param description A brief description of what the command does.
 * @param permission  The required permission to execute the command, if any.
 * @param parameters  A list of {@code ArgumentInfo} objects representing arguments
 *                    required or supported by the command.
 */
public record CommandData(String fullName, String description, String permission, List<ArgumentInfo> parameters) {

    /**
     * Converts the list of parameters defined in this command into a formatted string
     * representation. Each parameter is enclosed in angle brackets ("<parameter>") if it is
     * required, or square brackets ("[parameter]") if it is optional. Parameters are separated
     * by spaces in the resulting string.
     *
     * @return A string representing the list of parameters with their respective
     * required/optional status.
     */
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

