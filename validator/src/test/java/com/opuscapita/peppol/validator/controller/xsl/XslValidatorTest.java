package com.opuscapita.peppol.validator.controller.xsl;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("Duplicates")
public class XslValidatorTest {

    @Test
    public void validate() throws Exception {
        XslValidator validator = new XslValidator();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Templates templates;

        try (InputStream xsl = XslValidatorTest.class.getResourceAsStream("/xsl/NOGOV-UBL-T10.xsl")) {
            assertNotNull(xsl);
            Source xsltSource = new StreamSource(xsl);
            templates = transformerFactory.newTemplates(xsltSource);
        }

        try (InputStream data = XslValidatorTest.class.getResourceAsStream("/test_data/difi_files/OC-test-file.xml")) {
            assertNotNull(data);
            byte[] bytes = IOUtils.toByteArray(data);

            String result = validator.validate(bytes, templates).toString();

            // System.out.println(result);

            assertNotNull(result);
            assertTrue(result.contains("warning"));
            assertFalse(result.contains("fatal"));
            assertTrue(result.contains("id=\"NOGOV-T10-R003\""));
            assertTrue(result.contains("id=\"NOGOV-T10-R004\""));
            assertTrue(result.contains("id=\"NOGOV-T10-R005\""));
        }

        try (InputStream data = XslValidatorTest.class.getResourceAsStream("/test_data/difi_files/OC-test-file-NOK.xml")) {
            assertNotNull(data);
            byte[] bytes = IOUtils.toByteArray(data);

            String result = validator.validate(bytes, templates).toString();

            // System.out.println(result);

            assertNotNull(result);
            assertTrue(result.contains("warning"));
            assertFalse(result.contains("fatal"));
            assertTrue(result.contains("id=\"NOGOV-T10-R003\""));
            assertTrue(result.contains("id=\"NOGOV-T10-R004\""));
            assertTrue(result.contains("id=\"NOGOV-T10-R005\""));
        }
    }

}