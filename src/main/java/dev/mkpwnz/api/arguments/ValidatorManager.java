package dev.mkpwnz.api.arguments;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Manages the registration and creation of validators for command arguments,
 * based on annotations. This class follows a singleton design pattern to
 * ensure a single instance is used throughout the application.
 */
public class ValidatorManager {
    private static ValidatorManager instance;
    private final Map<Class<? extends Annotation>, Function<Annotation, ArgumentValidator<?>>> validatorFactories;

    private ValidatorManager() {
        this.validatorFactories = new HashMap<>();
        registerDefaultValidators();
    }

    /**
     * Retrieves the singleton instance of the ValidatorManager.
     * This method ensures that only one instance of ValidatorManager is created
     * throughout the application lifecycle and provides a global point of access to it.
     *
     * @return The singleton instance of ValidatorManager.
     */
    public static ValidatorManager getInstance() {
        if (instance == null) {
            instance = new ValidatorManager();
        }
        return instance;
    }

    /**
     * Registers the default argument validators for various types of arguments, including
     * numeric arguments, player arguments, string arguments, and string list arguments.
     * <p>
     * This method associates specific annotations (e.g., {@code NumberArg}, {@code PlayerArg},
     * {@code StringArg}, {@code StringListArg}) with their corresponding validator implementations.
     * These validators are responsible for validating input values based on the metadata
     * defined in the respective annotations.
     * <p>
     * For each argument type, this method creates a lambda function that processes the
     * annotation's attributes and initializes the corresponding validator instance.
     * The method makes use of the {@code registerValidator} method to store these mappings.
     */
    private void registerDefaultValidators() {
        registerValidator(NumberArg.class, annotation -> {
            NumberArg numberArg = (NumberArg) annotation;
            return new NumberArgValidator(
                    numberArg.name(),
                    numberArg.description(),
                    numberArg.required(),
                    numberArg.min(),
                    numberArg.max()
            );
        });

        registerValidator(PlayerArg.class, annotation -> {
            PlayerArg playerArg = (PlayerArg) annotation;
            return new PlayerArgValidator(
                    playerArg.name(),
                    playerArg.description(),
                    playerArg.required(),
                    playerArg.onlineOnly()
            );
        });

        registerValidator(StringArg.class, annotation -> {
            StringArg stringArg = (StringArg) annotation;
            return new StringArgValidator(
                    stringArg.name(),
                    stringArg.description(),
                    stringArg.required(),
                    stringArg.minLength(),
                    stringArg.maxLength()
            );
        });

        registerValidator(StringListArg.class, annotation -> {
            StringListArg stringArg = (StringListArg) annotation;
            return new StringListArgValidator(
                    stringArg.name(),
                    stringArg.description(),
                    stringArg.required(),
                    stringArg.allowedValues(),
                    stringArg.caseSensitive()
            );
        });

    }

    /**
     * Registers a custom argument validator for a specific annotation type. This method links
     * the specified annotation type with a factory function responsible for creating the corresponding
     * argument validator.
     *
     * @param <T>            The type of the annotation. Must extend {@link Annotation}.
     * @param annotationType The class of the annotation type for which a validator is being registered.
     *                       This defines the annotation that will trigger the specified factory function.
     * @param factory        A factory function that takes an {@link Annotation} instance and returns
     *                       an {@link ArgumentValidator}. The returned validator is responsible for
     *                       validation logic for arguments annotated with the specified annotation type.
     */
    public <T extends Annotation> void registerValidator(
            Class<T> annotationType,
            Function<Annotation, ArgumentValidator<?>> factory) {
        validatorFactories.put(annotationType, factory);
    }

    /**
     * Creates an instance of {@link ArgumentValidator} based on the provided annotation.
     * This method looks up a factory function registered for the annotation's type
     * and uses it to generate the corresponding {@link ArgumentValidator}.
     * If no factory is registered for the given annotation type, an exception is thrown.
     *
     * @param annotation The annotation based on which the {@link ArgumentValidator}
     *                   will be created. Must not be null.
     *
     * @return An instance of {@link ArgumentValidator} created for the specified annotation.
     *
     * @throws IllegalArgumentException If no registered factory exists for the annotation type.
     */
    public ArgumentValidator<?> createValidator(Annotation annotation) {
        Function<Annotation, ArgumentValidator<?>> factory = validatorFactories.get(annotation.annotationType());
        if (factory == null) {
            throw new IllegalArgumentException("Kein Validator registriert für: " + annotation.annotationType().getSimpleName());
        }
        return factory.apply(annotation);
    }

    /**
     * Checks whether a validator is registered for the specified annotation type.
     *
     * @param annotationType The class of the annotation for which the presence of a validator should be checked.
     *                       This defines the annotation type tied to a specific validation logic.
     *
     * @return {@code true} if a validator is registered for the given annotation type, {@code false} otherwise.
     */
    public boolean hasValidatorFor(Class<? extends Annotation> annotationType) {
        return validatorFactories.containsKey(annotationType);
    }
}

