package com.opuscapita.peppol.validator.validations.svefaktura1;

import com.opuscapita.peppol.validator.validations.common.BasicValidator;
import com.opuscapita.peppol.validator.validations.common.ValidationError;
import com.opuscapita.peppol.validator.validations.common.util.ValidationErrorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.naming.ConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bambr on 16.16.8.
 */
@Component
@Scope("prototype")
public class SveFaktura1Validator implements BasicValidator {
    private static final String SVEFAKTURA1_XSD_LOCATION = "/validation/svefaktura1/maindoc/SFTI-BasicInvoice-1.0.xsd";
    private final static String SBDH_XSD_LOCATION = "validation/SBDH/StandardBusinessDocumentHeader.xsd";
    private static final String XSL_LOCATION = "/validation/svefaktura1/out2016-02-17.xsl";

    @Autowired
    Svefaktura1ValidatorConfig svefaktura1ValidatorConfig;

    @Autowired
    Svefaktura1XsdValidator svefaktura1XsdValidator;

    List<ValidationError> errors = new ArrayList<>();

    @Override
    public List<ValidationError> getErrors() {
        return errors;
    }

    @Override
    public boolean validate(byte[] data) {

        svefaktura1XsdValidator.performXsdValidation(data);

        if (svefaktura1ValidatorConfig.getSchematronValidationEnabled()) {
            try {
                Document finalDocument = performSchematronValidation(data);
                extractErrorsAndWarnings(finalDocument);

            } catch (Exception e) {
                errors.add(ValidationErrorBuilder.aValidationError().withTitle("Svefaktura1 Schematron validation failed with exception").withDetails(e.getMessage()).build());
            }
            return errors.size() == 0;
        }
        return false;
    }

    private Document performSchematronValidation(byte[] data) throws ConfigurationException, TransformerException, ParserConfigurationException, SAXException, IOException {
        StreamSource styleSource = new StreamSource(new FileInputStream(new File(svefaktura1ValidatorConfig.getSchematronXslPath())));
        Transformer transformer = TransformerFactory.newInstance().newTransformer(styleSource);
        StringWriter resultWriter = new StringWriter();
        Result result = new StreamResult(resultWriter);
        transformer.transform(new StreamSource(new ByteArrayInputStream(data)), result);
        byte[] resultData = resultWriter.toString().getBytes(Charset.forName("UTF-8"));
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        documentFactory.setNamespaceAware(true);
        documentFactory.setValidating(false);
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        documentBuilder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {

            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        return documentBuilder.parse(new ByteArrayInputStream(resultData));
    }

    private void extractErrorsAndWarnings(Document resultDocument) {
        NodeList failedAsserts = resultDocument.getElementsByTagName("svrl:failed-assert");
        for (int i = 0; i < failedAsserts.getLength(); i++) {
            Node failedAssert = failedAsserts.item(i);
            NamedNodeMap attributes = failedAssert.getAttributes();
            boolean isError = false;
            ValidationError error = new ValidationError();
            error.setTitle("SveFaktura1 Schematron validation error");
            StringBuilder detailsStringBuilder = new StringBuilder();
            for (int j = 0; j < attributes.getLength(); j++) {
                Node attribute = attributes.item(j);
                if (attribute.getNodeName().equals("flag") && (attribute.getNodeValue().equals("fatal") || attribute.getNodeValue().equals("error"))) {
                    isError = true;
                    detailsStringBuilder.append(extractAssertMessage(failedAssert));
                    detailsStringBuilder.append("\n\rSeverity:\n\r");
                    detailsStringBuilder.append(attribute.getNodeValue());
                    detailsStringBuilder.append("\n\r");
                }
                if (isError) {
                    if (attribute.getNodeName().equals("location")) {
                        detailsStringBuilder.append("\n\rLocation:\n\r");
                        detailsStringBuilder.append(attribute.getNodeValue());
                    } else {
                        detailsStringBuilder.append(attribute.getNodeName()).append(": ").append(attribute.getNodeValue()).append("\n\r");
                    }
                }
                if (attribute.getNodeName().equals("flag") && attribute.getNodeValue().equals("warning")) {
                    System.out.println("WARNING: " + extractAssertMessage(failedAssert));
                }
            }
            if (isError) {
                error.setDetails(detailsStringBuilder.toString());
                errors.add(error);
            }
        }
    }

    private String extractAssertMessage(Node failedAssert) {
        return failedAssert.getChildNodes().item(1).getFirstChild().getNodeValue();
    }
}
