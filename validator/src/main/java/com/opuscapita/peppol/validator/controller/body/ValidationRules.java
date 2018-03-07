package com.opuscapita.peppol.validator.controller.body;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
@Component
@ConfigurationProperties(prefix = "peppol.validator.rules")
public class ValidationRules {
    @Value("${peppol.validator.rules.directory}")
    private String rulesDirectory;

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

    @PostConstruct
    private void validateFiles() {
        for (ValidationRule rule : map) {
            for (String fileName : rule.getRules()) {
                File ruleFile = new File(rulesDirectory, fileName);
                if (!ruleFile.exists()) {
                    throw new IllegalArgumentException(
                            "Rule file defined for " + rule.getProfile() + " but not available: " + ruleFile.getAbsolutePath());
                }
            }
        }
    }

}