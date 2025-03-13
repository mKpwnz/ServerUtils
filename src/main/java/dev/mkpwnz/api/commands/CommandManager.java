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

public class CommandManager implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final ValidatorManager validatorManager;

    private final Map<String, CommandHandler> commands = new HashMap<>();
    private final Map<String, CommandData> commandData = new HashMap<>();

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.validatorManager = ValidatorManager.getInstance();
    }

    private ArgumentValidator<?> getValidatorForParameter(Parameter parameter) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (validatorManager.hasValidatorFor(annotation.annotationType())) {
                return validatorManager.createValidator(annotation);
            }
        }
        return null;
    }

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

    public Map<String, CommandData> getRegisteredCommands() {
        return Collections.unmodifiableMap(commandData);
    }


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