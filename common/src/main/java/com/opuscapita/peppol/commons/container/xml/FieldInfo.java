package com.opuscapita.peppol.commons.container.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
public class FieldInfo {
    private String id;
    private List<String> paths;
    private boolean mandatory;
    private String mask;
    private String constant;

    @SuppressWarnings("unused")
    public FieldInfo() {}

    @SuppressWarnings("unused")
    public FieldInfo(@NotNull String id, boolean mandatory, @Nullable String mask, @Nullable String constant, @NotNull String... paths) {
        this.id = id;
        this.constant = constant;
        this.paths = Arrays.asList(paths);
        this.mandatory = mandatory;
        this.mask = mask;
    }

    FieldInfo(@NotNull String id, boolean mandatory, @Nullable String mask, @Nullable String constant, @Nullable List<String> paths) {
        this.id = id;
        this.constant = constant;
        this.paths = paths;
        this.mandatory = mandatory;
        this.mask = mask;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Nullable
    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    @Nullable
    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    @Nullable
    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldInfo)) return false;

        FieldInfo fieldInfo = (FieldInfo) o;

        if (mandatory != fieldInfo.mandatory) return false;
        if (!id.equals(fieldInfo.id)) return false;
        if (!paths.equals(fieldInfo.paths)) return false;
        if (mask != null ? !mask.equals(fieldInfo.mask) : fieldInfo.mask != null) return false;
        return constant != null ? constant.equals(fieldInfo.constant) : fieldInfo.constant == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + paths.hashCode();
        result = 31 * result + (mandatory ? 1 : 0);
        result = 31 * result + (mask != null ? mask.hashCode() : 0);
        result = 31 * result + (constant != null ? constant.hashCode() : 0);
        return result;
    }
}
