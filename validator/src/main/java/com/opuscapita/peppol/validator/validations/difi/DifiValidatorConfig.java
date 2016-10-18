package com.opuscapita.peppol.validator.validations.difi;

/**
 * Created by bambr on 16.6.10.
 */
public class DifiValidatorConfig {

    private final String difiValidationArtifactsPath;

    public DifiValidatorConfig(String difiValidationArtifactsPath) {
        this.difiValidationArtifactsPath = difiValidationArtifactsPath;
    }

    public String getDifiValidationArtifactsPath() {
        return difiValidationArtifactsPath;
    }
}
