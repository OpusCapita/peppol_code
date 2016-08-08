package com.opuscapita.peppol.commons.container.document.test;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

/**
 * @author Sergejs.Roze
 */
public class TestTypeNot extends BaseDocument {
    @Override
    public boolean fillFields() {
        return false;
    }

    @Override
    public boolean recognize(@NotNull Document document) {
        return false;
    }

    @NotNull
    @Override
    public Archetype getArchetype() {
        return null;
    }
}
