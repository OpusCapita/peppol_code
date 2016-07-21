package com.opuscapita.peppol.commons.container.document.impl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

/**
 * @author Sergejs.Roze
 */
public interface FieldsReader {

    boolean fillFields(@Nullable Node sbdh, @NotNull Node root, @NotNull BaseDocument base);

}
