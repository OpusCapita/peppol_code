package com.opuscapita.peppol.validator.controller.body;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author Sergejs.Roze
 */
//@Component
//@ConfigurationProperties(prefix = "peppol.validator.document")
public class RuleSetConfig {
    private Map<String, List<String>> types;

    @NotNull
    public Map<String, List<String>> getTypes() {
        return types;
    }

    public void setTypes(@NotNull Map<String, List<String>> types) {
        this.types = types;
    }

    /**
     * Returns set of rule names to be run against the given datatype or null if rules are not defined for
     * this type.
     *
     * @param documentType the document type as defined in configuration
     * @return the rule names or null
     */
    @Nullable
    List<String> getRules(@NotNull String documentType) {
        return types.get(documentType);
    }
}
