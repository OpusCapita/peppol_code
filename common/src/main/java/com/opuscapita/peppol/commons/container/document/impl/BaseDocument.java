package com.opuscapita.peppol.commons.container.document.impl;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergejs.Roze
 */
public abstract class BaseDocument {
    public abstract boolean acceptPathAndValue(@NotNull String path, @NotNull String value);

    public abstract DocumentInfo validateAndGet();
}
