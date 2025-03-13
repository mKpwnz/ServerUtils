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

/**
 * The {@code CommandHandler} class is responsible for managing and executing commands
 * within a command framework. It handles validation of input arguments, permission checks,
 * execution of command logic, and provides support for tab completion.
 * <p>
 * Each {@code CommandHandler} instance is associated with a specific command method
 * annotated with {@link Command}, and uses a combination of method reflection and
 * validators to process command inputs.
 */
public class CommandHandler {
    private final Object commandClass;
    private final Method method;
    private final Command commandAnnotation;
    private final List<ArgumentValidator<?>> parameterValidators;
    private final ValidatorManager validatorManager;

    /**
     * Constructs a new CommandHandler instance, responsible for mapping a method marked with the
     * {@link Command} annotation to its associated logic for execution and validation.
     *
     * @param commandClass      The instance of the class containing the command method. This holds
     *                          the context in which the command method is executed.
     * @param method            The method annotated with {@link Command} to be executed when the command is invoked.
     * @param commandAnnotation The {@link Command} annotation that contains metadata
     *                          about the command, such as its name, description, and permissions.
     */
    public CommandHandler(Object commandClass, Method method, Command commandAnnotation) {
        this.commandClass = commandClass;
        this.method = method;
        this.commandAnnotation = commandAnnotation;
        this.parameterValidators = new ArrayList<>();
        this.validatorManager = ValidatorManager.getInstance();

        initializeParameterValidators();
    }

    /**
     * Initializes the parameter validators for the command method, ensuring that every parameter
     * of the method (beyond the first, if it is of type {@code CommandSender}) has an associated
     * validator. If a parameter does not have a valid validator, an exception is thrown.
     * <p>
     * The validators are determined based on the annotations present on each parameter.
     * The method uses the available {@link ValidatorManager} to check for and create the appropriate
     * validators.
     *
     * @throws IllegalStateException If any parameter (beyond the first when of type {@code CommandSender})
     *                               does not have a valid validator.
     */
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

    /**
     * Executes a command based on the provided sender and arguments. The execution involves
     * validating permissions, checking required arguments, preparing parameters, and invoking
     * the corresponding method annotated with {@code @Command}.
     *
     * @param sender The entity initiating the command execution. It could be a player, console,
     *               or any other type extending {@code CommandSender}.
     * @param args   The arguments provided with the command. These are used to match and validate
     *               the parameters expected by the command.
     *
     * @return {@code true} if the command was executed successfully or ends without errors.
     * {@code false} if there are validation errors (e.g., insufficient arguments), or
     * an exception was raised during execution.
     */
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

    /**
     * Calculates and returns the count of required arguments based on the
     * validators associated with the command parameters.
     *
     * @return The number of required arguments as an integer. This is determined
     * by iterating over the parameter validators and counting the ones
     * marked as required.
     */
    private int getRequiredArgsCount() {
        int count = 0;
        for (ArgumentValidator<?> validator : parameterValidators) {
            if (validator.isRequired()) count++;
        }
        return count;
    }

    /**
     * Prepares the parameters required for invoking a command method by validating and converting
     * the provided arguments to the types expected by the method.
     * <p>
     * This method maps the incoming command arguments and sender to the corresponding method parameters.
     * It performs validation on the arguments using associated validators. If the validation fails or
     * required arguments are missing, the preparation is terminated, and an appropriate error message
     * is sent to the sender.
     *
     * @param sender The entity initiating the command. Represents the source of the command execution,
     *               such as a player, console, or other entities extending {@code CommandSender}.
     * @param args   An array of strings representing the arguments provided with the command. These
     *               will be validated and matched to the parameters of the method.
     *
     * @return An array of {@code Object} containing the prepared parameters for the method invocation.
     * Returns {@code null} if validation fails or if required arguments are missing.
     */
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

    /**
     * Provides a list of tab-completion suggestions for the command arguments.
     * This method uses the {@link ArgumentValidator}s associated with the command
     * parameters to generate possible completions for the current argument based on the input.
     *
     * @param sender The entity initiating the tab-completion request. It could be a player, console,
     *               or any other type extending {@code CommandSender}.
     * @param args   The list of arguments provided with the command so far. The method uses
     *               these to determine which parameter is currently being completed.
     *               The last element of the array represents the partially-typed input
     *               being completed.
     *
     * @return A list of potential tab-completion suggestions for the current argument.
     * The list may be empty if no suggestions are available or if the input does not match
     * any valid suggestions.
     */
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