package com.opuscapita.peppol.commons.container.document;

import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.validation.ValidationError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Sergejs.Roze
 */
public class DocumentWarning extends DocumentError {
    private static final long serialVersionUID = 4551706414937672794L;

    public DocumentWarning(@NotNull Endpoint source, @NotNull String message) {
        this(source, message, null);
    }

    public DocumentWarning(@NotNull Endpoint source, @NotNull String message, @Nullable ValidationError warning) {
        super(source, message, warning);
    }

    @Override
    public String toString() {
        return getSource() + " warning: " + getMessage();
    }

}
