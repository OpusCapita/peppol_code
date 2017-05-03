package com.opuscapita.peppol.validator.validations.svefaktura1;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.validation.BasicValidator;
import com.opuscapita.peppol.commons.validation.ValidationError;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bambr on 16.16.8.
 */

@SuppressWarnings("unused")
public class Svefaktura1Validator implements BasicValidator {
    private static final String SVEFAKTURA1_XSD_LOCATION = "/validation/svefaktura1/maindoc/SFTI-BasicInvoice-1.0.xsd";
    private final static String SBDH_XSD_LOCATION = "validation/SBDH/StandardBusinessDocumentHeader.xsd";
    private static final String XSL_LOCATION = "/validation/svefaktura1/out2016-02-17.xsl";

    private static final Logger logger = LoggerFactory.getLogger(Svefaktura1Validator.class);

    private final Svefaktura1ValidatorConfig svefaktura1ValidatorConfig;
    private final Svefaktura1XsdValidator svefaktura1XsdValidator;

    public Svefaktura1Validator(@NotNull Svefaktura1ValidatorConfig svefaktura1ValidatorConfig,
                                @NotNull Svefaktura1XsdValidator svefaktura1XsdValidator) {
        this.svefaktura1ValidatorConfig = svefaktura1ValidatorConfig;
        this.svefaktura1XsdValidator = svefaktura1XsdValidator;
    }

    @Override
    @NotNull
    public ContainerMessage validate(@NotNull ContainerMessage cm, @NotNull byte[] data) {
        List<DocumentError> result = new ArrayList<>();
        svefaktura1XsdValidator.performXsdValidation(cm, data);

        if (svefaktura1ValidatorConfig.getSchematronValidationEnabled()) {
            try {
                Document finalDocument = performSchematronValidation(data);
                extractErrorsAndWarnings(finalDocument, cm);
            } catch (Exception e) {
                cm.addError("Svefaktura1 Schematron validation failed with exception: " + e.getMessage());
            }

        }
        return cm;
    }

    private Document performSchematronValidation(byte[] data) throws ConfigurationException, TransformerException, ParserConfigurationException,
            SAXException, IOException {
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
                if (exception != null) {
                    throw exception;
                }
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                if (exception != null) {
                    throw exception;
                }
            }
        });
        return documentBuilder.parse(new ByteArrayInputStream(resultData));
    }

    @SuppressWarnings("ConstantConditions")
    private void extractErrorsAndWarnings(Document resultDocument, ContainerMessage cm) {
        NodeList failedAsserts = resultDocument.getElementsByTagName("svrl:failed-assert");
        for (int i = 0; i < failedAsserts.getLength(); i++) {
            Node failedAssert = failedAsserts.item(i);
            NamedNodeMap attributes = failedAssert.getAttributes();
            boolean hasErrors = false;
            ValidationError error = new ValidationError().withTitle("Svefaktura1 schematron validation error");

            for (int j = 0; j < attributes.getLength(); j++) {
                Node attribute = attributes.item(j);
                if (attribute.getNodeName().equals("flag") && (attribute.getNodeValue().equals("fatal") || attribute.getNodeValue().equals("error"))) {
                    hasErrors = true;
                    error.withTest(extractAssertMessage(failedAssert));
                    error.withFlag(attribute.getNodeValue());
                }
                if (hasErrors) {
                    if (attribute.getNodeName().equals("location")) {
                        error.withLocation(attribute.getNodeValue());
                    }
                }
                if (attribute.getNodeName().equals("flag") && attribute.getNodeValue().equals("warning")) {
                    String warningMessage = extractAssertMessage(failedAssert);
                    cm.addWarning(new DocumentWarning(cm.getProcessingInfo().getCurrentEndpoint(), warningMessage));
                    logger.info("Validation warning: " + warningMessage);
                }
            }
            if (hasErrors) {
                cm.addError(error.toDocumentError(cm.getProcessingInfo().getCurrentEndpoint()));
            }
        }
    }

    private String extractAssertMessage(Node failedAssert) {
        return failedAssert.getChildNodes().item(1).getFirstChild().getNodeValue();
    }

    @SuppressWarnings("unused")
    private void debugLogDomDocument(Document document) throws UnsupportedEncodingException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "templates");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(document),
                new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
    }
}
