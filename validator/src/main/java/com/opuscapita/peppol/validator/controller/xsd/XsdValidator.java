package com.opuscapita.peppol.validator.controller.xsd;

import com.opuscapita.peppol.commons.validation.ValidationError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.StringReader;

/**
 * @author Sergejs.Roze
 */
//@Component
public class XsdValidator {

    /**
     * Validates data against given schema.
     *
     * @param data the input data
     * @param schema the XSD schema
     * @return validation error if any or null if there was no XSD error
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public ValidationError validate(@NotNull byte[] data, @NotNull Schema schema) {
        Validator validator = schema.newValidator();
        Source source = new StreamSource(new StringReader(new String(data)));

        try {
            validator.validate(source);
        } catch (Exception e) {
            return new ValidationError("SBDH validation failure").withText(e.getMessage());
        }

        return null;
    }

}
