package dev.mkpwnz.api.arguments;

public class ValidationResult<T> {
    private final boolean success;
    private final T value;
    private final String errorMessage;

    private ValidationResult(boolean success, T value, String errorMessage) {
        this.success = success;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public static <T> ValidationResult<T> success(T value) {
        return new ValidationResult<>(true, value, null);
    }

    public static <T> ValidationResult<T> error(String message) {
        return new ValidationResult<>(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getValue() {
        return value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
