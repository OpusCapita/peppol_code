package com.opuscapita.peppol.validator.validations.svefaktura1;

import com.opuscapita.peppol.validator.validations.common.BasicValidator;
import com.opuscapita.peppol.validator.validations.common.ValidationError;

import java.util.List;

/**
 * Created by bambr on 16.16.8.
 */
public class SveFaktura1Validator implements BasicValidator {
    private static final String SVEFAKTURA1_XSD_LOCATION = "/validation/svefaktura1/maindoc/SFTI-BasicInvoice-1.0.xsd";
    private final static String SBDH_XSD_LOCATION = "validation/SBDH/StandardBusinessDocumentHeader.xsd";
    private static final String XSL_LOCATION = "/validation/svefaktura1/out2016-02-17.xsl";

    @Override
    public List<ValidationError> getErrors() {
        return null;
    }

    @Override
    public boolean validate(byte[] data) {
        return false;
    }
}
