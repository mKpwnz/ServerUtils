package dev.mkpwnz.api.commands;

import dev.mkpwnz.api.arguments.ArgumentInfo;
import dev.mkpwnz.api.arguments.ArgumentValidator;
import dev.mkpwnz.api.arguments.ValidatorManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Implements the functionality for managing and executing commands within the Bukkit/Spigot framework.
 * This class is responsible for registering, validating, and delegating execution of commands.
 * It acts as the bridge between the plugin's custom commands and the Bukkit/Spigot command system.
 */
public class CommandManager implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final ValidatorManager validatorManager;


    private final Map<String, CommandHandler> commands = new HashMap<>();
    private final Map<String, CommandData> commandData = new HashMap<>();

    /**
     * Constructs a new instance of the CommandManager, responsible for
     * managing command registrations, argument validation, and command execution.
     *
     * @param plugin The JavaPlugin instance associated with this CommandManager.
     *               This is required for registering commands within the Bukkit/Spigot API.
     */
    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.validatorManager = ValidatorManager.getInstance();
    }

    /**
     * Retrieves an {@link ArgumentValidator} suitable for the given method parameter
     * by analyzing its annotations. This method iterates through all annotations
     * applied to the parameter and finds the first one that has an associated
     * validator registered in the {@code validatorManager}.
     *
     * @param parameter The {@link Parameter} representing the method parameter for which
     *                  validation is needed. This object contains metadata about the
     *                  parameter, including its annotations.
     *
     * @return An instance of {@link ArgumentValidator} appropriate for the given
     * parameter based on its annotations, or {@code null} if no suitable validator
     * is found.
     */
    private ArgumentValidator<?> getValidatorForParameter(Parameter parameter) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (validatorManager.hasValidatorFor(annotation.annotationType())) {
                return validatorManager.createValidator(annotation);
            }
        }
        return null;
    }

    /**
     * Registers a command class and its associated methods annotated with the {@link Command}
     * annotation to the command system. This method extracts metadata from the annotations
     * and initializes necessary objects to handle command execution and tab completion.
     *
     * @param commandClass The object containing methods annotated with {@link Command}.
     *                     Methods in this class are processed and registered as executable
     *                     commands within the command system. The class must include one
     *                     or more methods with the {@link Command} annotation.
     */
    public void registerCommand(Object commandClass) {
        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            Command commandAnnotation = method.getAnnotation(Command.class);
            if (commandAnnotation != null) {
                String fullCommandName = buildCommandName(commandAnnotation);
                CommandHandler handler = new CommandHandler(commandClass, method, commandAnnotation);
                commands.put(fullCommandName, handler);

                List<ArgumentInfo> parameters = new ArrayList<>();
                for (Parameter param : method.getParameters()) {
                    if (param.getType() == CommandSender.class) {
                        continue;
                    }

                    ArgumentInfo argInfo = null;
                    for (Annotation annotation : param.getAnnotations()) {
                        if (validatorManager.hasValidatorFor(annotation.annotationType())) {
                            try {
                                Method nameMethod = annotation.annotationType().getMethod("name");
                                Method descriptionMethod = annotation.annotationType().getMethod("description");
                                Method requiredMethod = annotation.annotationType().getMethod("required");

                                String name = (String) nameMethod.invoke(annotation);
                                String description = (String) descriptionMethod.invoke(annotation);
                                boolean required = (boolean) requiredMethod.invoke(annotation);

                                argInfo = new ArgumentInfo(name, description, required);
                                break;
                            } catch (Exception e) {
                                plugin.getLogger().warning("Fehler beim Verarbeiten der Parameter-Annotation: " + e.getMessage());
                            }
                        }
                    }

                    if (argInfo != null) {
                        parameters.add(argInfo);
                    }
                }

                commandData.put(fullCommandName, new CommandData(
                        fullCommandName,
                        commandAnnotation.description(),
                        commandAnnotation.permission(),
                        parameters
                ));

                StringBuilder parameterInfo = new StringBuilder();
                for (ArgumentInfo arg : parameters) {
                    if (!parameterInfo.isEmpty()) {
                        parameterInfo.append(" ");
                    }
                    parameterInfo.append(arg.required() ?
                            "<" + arg.name() + ">" :
                            "[" + arg.name() + "]");
                }

                if (commandAnnotation.parent().length == 0) {
                    Objects.requireNonNull(plugin.getCommand(commandAnnotation.name())).setExecutor(this);
                    Objects.requireNonNull(plugin.getCommand(commandAnnotation.name())).setTabCompleter(this);
                }

                plugin.getLogger().info(String.format(
                        "§aRegistriere Command: /%s %s §7(Permission: %s)",
                        fullCommandName.replace(".", " "),
                        parameterInfo,
                        commandAnnotation.permission().isEmpty() ? "keine" : commandAnnotation.permission()
                ));
            }
        }
    }

    /**
     * Retrieves an immutable map of registered commands and their associated metadata.
     * The keys in the map are the full names of the commands, and the values are
     * {@link CommandData} objects containing the command metadata.
     *
     * @return A {@code Map<String, CommandData>} representing all registered commands,
     * where the key is the full name of the command and the value is its associated
     * metadata. The returned map is immutable and cannot be modified.
     */
    public Map<String, CommandData> getRegisteredCommands() {
        return Collections.unmodifiableMap(commandData);
    }

    /**
     * Builds the full name of a command by concatenating its parent commands and its own name.
     * The command name and parent names are converted to lowercase and separated by a period (".").
     *
     * @param annotation The {@link Command} annotation containing metadata about the command.
     *                   This includes the command's name and its parent commands.
     *
     * @return A {@code String} representing the fully qualified command name. If the command has
     * parent commands, they are prefixed to the name and separated by periods. If there are
     * no parent commands, the command name is returned directly in lowercase.
     */
    private String buildCommandName(Command annotation) {
        StringBuilder commandName = new StringBuilder();

        for (String parent : annotation.parent()) {
            if (!commandName.isEmpty()) {
                commandName.append(".");
            }
            commandName.append(parent.toLowerCase());
        }

        if (!commandName.isEmpty()) {
            commandName.append(".");
        }
        commandName.append(annotation.name().toLowerCase());

        return commandName.toString();
    }

    /**
     * Executes a command sent by a {@link CommandSender}. This method handles command execution
     * by resolving the full command name (including subcommands) and delegating to an appropriate
     * {@link CommandHandler} if a matching command is found.
     *
     * @param sender  The {@link CommandSender} who executed the command. This can represent a player,
     *                the console, or any entity capable of sending commands.
     * @param command The {@link org.bukkit.command.Command} object representing the base command
     *                that was executed.
     * @param label   The exact alias of the command used by the sender. This can assist in identifying
     *                how the command was called if it has multiple aliases.
     * @param args    An array of {@code String} containing the arguments passed with the command.
     *                These arguments are used to determine subcommands or additional parameters
     *                for the command.
     *
     * @return {@code true} if the command was successfully executed and handled by a valid
     * {@link CommandHandler}; {@code false} otherwise, indicating that no
     * valid handler was found for the command.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.Command command, @NotNull String label, String[] args) {
        String baseCommand = command.getName().toLowerCase();
        String fullCommand = baseCommand;

        if (args.length > 0) {
            fullCommand = baseCommand + "." + String.join(".", args);
        }

        String matchingCommand = null;
        String[] parts = fullCommand.split("\\.");

        for (int i = parts.length; i > 0; i--) {
            String testCommand = String.join(".", Arrays.copyOfRange(parts, 0, i));
            if (commands.containsKey(testCommand)) {
                matchingCommand = testCommand;
                break;
            }
        }

        if (matchingCommand != null) {
            CommandHandler handler = commands.get(matchingCommand);
            String[] remainingArgs = Arrays.copyOfRange(args,
                    matchingCommand.split("\\.").length - 1,
                    args.length);
            return handler.execute(sender, remainingArgs);
        }

        return false;
    }

    /**
     * Handles tab completion for commands and provides a list of suggestions based on
     * the current input. This method processes the command structure, matches subcommands,
     * or delegates tab completion to a specific command handler if applicable.
     *
     * @param sender  The {@link CommandSender} who initiated the tab completion. This can be
     *                a player, the console, or another entity capable of executing commands.
     * @param command The {@link org.bukkit.command.Command} object representing the base
     *                command for which tab completion is being performed.
     * @param alias   The alias of the command that was typed by the {@code sender}.
     * @param args    An array of {@code String} arguments provided by the {@code sender},
     *                representing the current state of the input being typed.
     *
     * @return A {@code List<String>} containing possible completions based on the current input.
     * If no suggestions are found, an empty list is returned.
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.Command command, @NotNull String alias, String[] args) {
        String baseCommand = command.getName().toLowerCase();
        List<String> suggestions = new ArrayList<>();

        String currentPath = baseCommand;
        if (args.length > 1) {
            currentPath += "." + String.join(".", Arrays.copyOfRange(args, 0, args.length - 1));
        }

        String prefix = currentPath + ".";
        String lastArg = args.length > 0 ? args[args.length - 1].toLowerCase() : "";

        for (String registeredCommand : commands.keySet()) {
            if (registeredCommand.startsWith(prefix)) {
                String subCommand = registeredCommand.substring(prefix.length());
                if (!subCommand.contains(".") && subCommand.startsWith(lastArg)) {
                    suggestions.add(subCommand);
                }
            }
        }

        if (suggestions.isEmpty()) {
            String fullCommand;
            if (args.length > 0) {
                String argPath = String.join(".", Arrays.stream(args)
                        .filter(arg -> !arg.isEmpty())
                        .toArray(String[]::new));
                fullCommand = argPath.isEmpty() ? baseCommand : baseCommand + "." + argPath;
            } else {
                fullCommand = baseCommand;
            }

            CommandHandler handler = commands.get(fullCommand);

            if (handler != null) {
                return handler.tabComplete(sender, args);
            }
        }

        return suggestions;
    }
}