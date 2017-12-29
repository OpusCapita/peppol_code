package com.opuscapita.peppol.validator.controller.body;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sergejs.Roze
 */
@Component
@ConfigurationProperties(prefix = "peppol.validator.document")
public class RuleSetConfig {
    private Map<String, List<String>> types;

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
    // there is a bug in Spring that prevents dots from being used in keys and values in YAML
    // therefore all dots are replaced with pipes
    List<String> getRules(@NotNull String documentType) {
        if (types == null) {
            throw new IllegalStateException("Failed to load validation rule sets");
        }
        documentType = documentType.replaceAll("\\.", "\\|");
        List<String> result = types.get(documentType);
        if (result == null) {
            throw new IllegalStateException("Validation settings not found for type: " + documentType);
        }
        return result.stream().map(v -> v.replaceAll("\\|", ".")).collect(Collectors.toList());
    }
}