package dev.mkpwnz.api.arguments;

/**
 * Represents the result of a validation, which includes information about
 * whether the validation was successful, the validated value or an error message.
 *
 * @param <T> The type of the value associated with a successful validation.
 */
public class ValidationResult<T> {
    private final boolean success;
    private final T value;
    private final String errorMessage;

    /**
     * Constructs a ValidationResult with the given success state, value, and error message.
     *
     * @param success      Indicates whether the validation was successful.
     * @param value        The validated value, if the validation was successful. Null otherwise.
     * @param errorMessage The error message, if the validation failed. Null otherwise.
     */
    private ValidationResult(boolean success, T value, String errorMessage) {
        this.success = success;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a successful validation result containing the specified value.
     *
     * @param <T>   The type of the value associated with the successful validation.
     * @param value The value associated with the successful validation.
     *              This is the validated data that passed the validation check.
     *
     * @return A {@code ValidationResult} instance representing a successful validation
     * with the specified value.
     */
    public static <T> ValidationResult<T> success(T value) {
        return new ValidationResult<>(true, value, null);
    }

    /**
     * Creates a validation result indicating an error with the specified message.
     *
     * @param <T>     The type of the value associated with a successful validation.
     *                This is a generic type parameter and is unused in this method
     *                because it represents a failed validation.
     * @param message The error message describing the reason for the validation failure.
     *                This message provides details about what caused the validation to fail.
     *
     * @return A {@code ValidationResult} instance representing a failed validation,
     * including the provided error message.
     */
    public static <T> ValidationResult<T> error(String message) {
        return new ValidationResult<>(false, null, message);
    }

    /**
     * Determines whether the validation was successful.
     *
     * @return {@code true} if the validation was successful; {@code false} otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Retrieves the value associated with the validation result.
     * If the validation was successful, this value represents the validated data.
     * If the validation failed, this value will typically be null.
     *
     * @return The value of type T if the validation was successful; null otherwise.
     */
    public T getValue() {
        return value;
    }

    /**
     * Retrieves the error message associated with a validation result.
     * If the validation failed, this method returns the corresponding error message.
     * If the validation was successful, this may return null.
     *
     * @return The error message as a String if the validation failed; otherwise null.
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
