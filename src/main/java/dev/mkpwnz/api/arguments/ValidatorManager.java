package dev.mkpwnz.api.arguments;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ValidatorManager {
    private static ValidatorManager instance;
    private final Map<Class<? extends Annotation>, Function<Annotation, ArgumentValidator<?>>> validatorFactories;

    private ValidatorManager() {
        this.validatorFactories = new HashMap<>();
        registerDefaultValidators();
    }

    public static ValidatorManager getInstance() {
        if (instance == null) {
            instance = new ValidatorManager();
        }
        return instance;
    }

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

    public <T extends Annotation> void registerValidator(
            Class<T> annotationType,
            Function<Annotation, ArgumentValidator<?>> factory) {
        validatorFactories.put(annotationType, factory);
    }

    public ArgumentValidator<?> createValidator(Annotation annotation) {
        Function<Annotation, ArgumentValidator<?>> factory = validatorFactories.get(annotation.annotationType());
        if (factory == null) {
            throw new IllegalArgumentException("Kein Validator registriert für: " + annotation.annotationType().getSimpleName());
        }
        return factory.apply(annotation);
    }

    public boolean hasValidatorFor(Class<? extends Annotation> annotationType) {
        return validatorFactories.containsKey(annotationType);
    }
}

