package com.opuscapita.peppol.commons.container.document;

import com.google.gson.annotations.Since;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.validation.ValidationError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * @author Sergejs.Roze
 */
public class DocumentError implements Serializable {
    private static final long serialVersionUID = 1522769757163697308L;
    public static final String ERROR_SEPARATOR = " error: ";

    @Since(1.0)
    private final Endpoint source;
    @Since(1.0)
    private final String message;
    @Since(1.0)
    private final ValidationError validationError;

    public DocumentError(@NotNull Endpoint source, @NotNull String message) {
        this(source, message, null);
    }

    public DocumentError(@NotNull Endpoint source, @NotNull String message, @Nullable ValidationError validationError) {
        this.source = source;
        this.message = message;
        this.validationError = validationError;
    }

    @NotNull
    public Endpoint getSource() {
        return source;
    }

    @NotNull
    public String getMessage() {
        if (validationError != null) {
            return validationError.toString();
        }
        return message;
    }

    @Nullable
    public ValidationError getValidationError() {
        return validationError;
    }

    @Override
    public String toString() {
        return source + ERROR_SEPARATOR + message;
    }
}
