package com.opuscapita.peppol.commons.container.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
public class DocumentTemplate {
    private final List<FieldInfo> fields = new ArrayList<>();
    private String name;
    private String root;

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public List<FieldInfo> getFields() {
        return Collections.unmodifiableList(fields);
    }

    @Nullable
    public String getRoot() {
        return root;
    }

    public void setRoot(@Nullable String root) {
        this.root = root;
    }
}
