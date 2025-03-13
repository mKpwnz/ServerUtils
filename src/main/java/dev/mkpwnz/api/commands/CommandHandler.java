package dev.mkpwnz.api.commands;

import dev.mkpwnz.api.arguments.ArgumentValidator;
import dev.mkpwnz.api.arguments.ValidationResult;
import dev.mkpwnz.api.arguments.ValidatorManager;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    private final Object commandClass;
    private final Method method;
    private final Command commandAnnotation;
    private final List<ArgumentValidator<?>> parameterValidators;
    private final ValidatorManager validatorManager;

    public CommandHandler(Object commandClass, Method method, Command commandAnnotation) {
        this.commandClass = commandClass;
        this.method = method;
        this.commandAnnotation = commandAnnotation;
        this.parameterValidators = new ArrayList<>();
        this.validatorManager = ValidatorManager.getInstance();

        initializeParameterValidators();
    }

    private void initializeParameterValidators() {
        Parameter[] parameters = method.getParameters();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        int startIndex = parameters[0].getType() == CommandSender.class ? 1 : 0;

        for (int i = startIndex; i < parameters.length; i++) {
            boolean validatorFound = false;
            for (Annotation annotation : parameterAnnotations[i]) {
                if (validatorManager.hasValidatorFor(annotation.annotationType())) {
                    parameterValidators.add(validatorManager.createValidator(annotation));
                    validatorFound = true;
                    break;
                }
            }

            if (!validatorFound) {
                throw new IllegalStateException(
                        "Kein gültiger Validator gefunden für Parameter: " + parameters[i].getName()
                );
            }
        }
    }


    public boolean execute(CommandSender sender, String[] args) {
        if (!commandAnnotation.permission().isEmpty() &&
                !sender.hasPermission(commandAnnotation.permission())) {
            sender.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }

        int requiredArgs = getRequiredArgsCount();
        if (args.length < requiredArgs) {
            sender.sendMessage("§cZu wenige Argumente! Benötigt: " + requiredArgs);
            return false;
        }

        Object[] parameters = prepareParameters(sender, args);

        try {
            method.invoke(commandClass, parameters);
            return true;
        } catch (Exception e) {
            sender.sendMessage("§cEs ist ein Fehler aufgetreten: " + e.getMessage());
            sender.getServer().getLogger().throwing(CommandHandler.class.getName(), "execute", e);
            return false;
        }

    }

    private int getRequiredArgsCount() {
        int count = 0;
        for (ArgumentValidator<?> validator : parameterValidators) {
            if (validator.isRequired()) count++;
        }
        return count;
    }

    private Object[] prepareParameters(CommandSender sender, String[] args) {
        Parameter[] methodParams = method.getParameters();
        Object[] parameters = new Object[methodParams.length];
        int argIndex = 0;

        for (int i = 0; i < methodParams.length; i++) {
            if (methodParams[i].getType() == CommandSender.class) {
                parameters[i] = sender;
                continue;
            }

            if (argIndex >= args.length) {
                if (parameterValidators.get(i - 1).isRequired()) {
                    sender.sendMessage("§cFehlendes erforderliches Argument: " +
                            parameterValidators.get(i - 1).getName());
                    return null;
                }
                continue;
            }

            ArgumentValidator<?> validator = parameterValidators.get(i - 1);
            ValidationResult<?> result = validator.validate(args[argIndex], sender);

            if (!result.isSuccess()) {
                sender.sendMessage("§cUngültiges Argument '" + validator.getName() +
                        "': " + result.getErrorMessage());
                return null;
            }
            parameters[i] = result.getValue();
            argIndex++;
        }

        return parameters;
    }


    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) return new ArrayList<>();

        int parentCommandCount = commandAnnotation.parent().length;
        int actualParamIndex = args.length - parentCommandCount - 1;

        if (actualParamIndex >= 0 && actualParamIndex < parameterValidators.size()) {
            ArgumentValidator<?> validator = parameterValidators.get(actualParamIndex);
            List<String> completions = validator.getTabCompletions(sender);
            String current = args[args.length - 1].toLowerCase();

            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(current))
                    .collect(java.util.stream.Collectors.toList());
        }
        return new ArrayList<>();
    }
}