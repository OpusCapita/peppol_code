package com.opuscapita.peppol.validator.controller.xsd;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.validator.controller.cache.XsdRepository;
import com.opuscapita.peppol.validator.controller.util.DocumentSplitter;
import org.junit.Test;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.fail;

/**
 * @author Sergejs.Roze
 */
public class HeaderValidatorTest {
    private DocumentSplitter splitter = new DocumentSplitter(XMLInputFactory.newFactory());
    private HeaderValidator validator;


    @Test
    public void validate() throws Exception {
        XsdRepository xsdRepository = name -> SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                new File(HeaderValidatorTest.class.getResource("/rules/xsd").getFile(),"StandardBusinessDocumentHeader.xsd"));
        XsdValidator xsdValidator = new XsdValidator();
        validator = new HeaderValidator(xsdValidator, xsdRepository);

        ContainerMessage cm = new ContainerMessage("meatdata", "test.xml", Endpoint.TEST);
        cm.setDocumentInfo(new DocumentInfo());

        checkNoErrors("/test_data/difi_files/Valids-OC-test-file.xml", cm, "Invoice"); // no header
        checkNoErrors("/test_data/difi_files/Valids3-list-ejt.xml", cm, "CreditNote"); // valid header
    }

    private void checkNoErrors(String fileName, ContainerMessage cm, String tag) throws Exception {
        System.out.println("Testing file " + fileName);
        try (InputStream inputStream = HeaderValidatorTest.class.getResourceAsStream(fileName)) {
            DocumentSplitter.Result parts = splitter.split(inputStream, tag);
            validator.validate(parts.getSbdh(), cm);
        }
        if (cm.hasErrors()) {
            //noinspection ConstantConditions
            for (DocumentError error : cm.getDocumentInfo().getErrors()) {
                System.err.println("ERROR: " + error.toString());
            }
            fail("The file header is valid but validation failed");
        }

    }

}
