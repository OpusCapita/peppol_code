package com.opuscapita.peppol.commons.container.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class DocumentTemplate {
    private List<FieldInfo> fields = new ArrayList<>();
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
        return fields;
    }

    public void setFields(List<FieldInfo> fields) {
        this.fields = fields;
    }

    @Nullable
    public String getRoot() {
        return root;
    }

    public void setRoot(@Nullable String root) {
        this.root = root;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentTemplate)) return false;

        DocumentTemplate template = (DocumentTemplate) o;

        if (!fields.equals(template.fields)) return false;
        if (name != null ? !name.equals(template.name) : template.name != null) return false;
        return root != null ? root.equals(template.root) : template.root == null;
    }

    @Override
    public int hashCode() {
        int result = fields.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (root != null ? root.hashCode() : 0);
        return result;
    }
}
