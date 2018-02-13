package com.opuscapita.peppol.validator.controller.body;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Sergejs.Roze
 */
@Component
@ConfigurationProperties(prefix = "peppol.validator.rules")
public class ValidationRules {
    private List<ValidationRule> map;

    public List<ValidationRule> getMap() {
        return map;
    }

    public void setMap(List<ValidationRule> map) {
        this.map = map;
    }

    @Nullable
    public ValidationRule getByDocumentType(@NotNull String documentType) {
        return map.stream()
                .filter(r -> documentType.equals(r.getProfile()))
                .findAny()
                .orElse(null);
    }
}