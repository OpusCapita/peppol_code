package com.opuscapita.peppol.validator.validations.svefaktura1;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.annotation.Resource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by bambr on 16.19.9.
 */
@Resource
@ConditionalOnProperty(name = "peppol.validator.difi.enabled", havingValue = "true", matchIfMissing = true)
public class Svefaktura1ValidatorConfig {
    private final String schematronXslPath;
    private final Boolean schematronValidationEnabled;
    private final String svefaktura1XsdPath;

    public Svefaktura1ValidatorConfig(Boolean schematronValidationEnabled, String schematronXslPath, String svefaktura1XsdPath) {
        this.schematronValidationEnabled = schematronValidationEnabled;
        this.schematronXslPath = schematronXslPath;
        this.svefaktura1XsdPath = svefaktura1XsdPath;
        if (schematronValidationEnabled && (isBadFile(schematronXslPath) || !new File(svefaktura1XsdPath).exists())) {
            throw new IllegalArgumentException("When 'svefaktura1.schematron.enabled' property is set to 'true', make sure you also set value for: 'svefaktura1.schematron.path' property and it is pointing to valid XSL schematron file");
        }
    }

    public String getSvefaktura1XsdPath() {
        return svefaktura1XsdPath;
    }

    private boolean isBadFile(String fileToCheck) {
        return fileToCheck == null || fileToCheck.isEmpty() || isInvalidXslFile(fileToCheck);
    }

    private boolean isInvalidXslFile(String schematronXslFilePath) {
        try {
            File schematronXslFile = new File(schematronXslFilePath);
            StreamSource styleSource = new StreamSource(new FileInputStream(schematronXslFile));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(styleSource);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    public String getSchematronXslPath() {
        return schematronXslPath;
    }

    public Boolean getSchematronValidationEnabled() {
        return schematronValidationEnabled;
    }
}
