package com.opuscapita.peppol.commons.container.document;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.xml.DocumentParser;
import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class DocumentLoaderTest {
    private final DocumentLoader loader;

    public DocumentLoaderTest() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        ErrorHandler errorHandler = mock(ErrorHandler.class);
        DocumentTemplates templates = new DocumentTemplates(errorHandler, new Gson());
        DocumentParser parser = new DocumentParser(factory.newSAXParser(), templates);
        loader = new DocumentLoader(parser);
    }


    @Test
    public void testTypes() throws Exception {
        List<String> list;

        // test single document using this code
        // list = Arrays.asList("/invalid/simpler_invoicing_files/invalids5.xml");
        // checkTypes(list, UblDocument.class);

        list = Arrays.asList(
                "/valid/valid.german.xml",
                "/valid/austria_files/Valid1.xml",
                "/valid/austria_files/Valid2.xml",
                "/valid/austria_files/Valid3.xml",
                "/valid/austria_files/Valid4.xml",
                "/valid/austria_files/Valid5.xml",
                "/valid/austria_files/Valid-AT-UBL_Austria_profile-bii04-invoice.xml",
                "/valid/simpler_invoicing_files/Valid1.xml",
                "/valid/simpler_invoicing_files/Valid2.xml",
                "/valid/simpler_invoicing_files/Valid3.xml",
                "/valid/simpler_invoicing_files/Valid4.xml",
                "/valid/simpler_invoicing_files/Valid5.xml",
                "/invalid/austria_files/invalids1.xml",
                "/invalid/austria_files/invalids2.xml",
                "/invalid/austria_files/invalids3.xml",
                "/invalid/austria_files/invalids4.xml",
                "/invalid/austria_files/invalids5.xml",
                "/invalid/simpler_invoicing_files/SI-inv-v1.1.3-Valid-version.xml.no.sbdh.sad.smiley",
                "/invalid/simpler_invoicing_files/invalids1.xml",
                "/invalid/simpler_invoicing_files/invalids2.xml",
                "/invalid/simpler_invoicing_files/invalids3.xml",
                "/invalid/simpler_invoicing_files/invalids4.xml",
                "/invalid/simpler_invoicing_files/invalids5.xml"
        );
        checkTypes(list, Archetype.PEPPOL_BIS);

        list = Arrays.asList(
                "/valid/ehf.xml",
                "/valid/valid_sbdh_2.1.xml",
                "/valid/ehf_2.0_bii4_no.xml",
                "/valid/ehf_2.0_bii5_no.xml"
        );
        checkTypes(list, Archetype.EHF);

        list = Arrays.asList(
                "/valid/svefaktura1.xml",
                "/valid/sv1_with_attachment.xml"
        );
        checkTypes(list, Archetype.SVEFAKTURA1);

        list = Arrays.asList(
                "/invalid/not_xml.txt",
                "/invalid/random.xml"
        );
        checkTypes(list, Archetype.INVALID);

    }

    private void checkTypes(List<String> list, Archetype archetype) throws Exception {
        for (String fileName : list) {
            try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream(fileName)) {
                DocumentInfo document = loader.load(inputStream, fileName, new Endpoint("test", ProcessType.TEST));
                System.out.println(document.getErrors().stream().map(DocumentError::getMessage).collect(Collectors.joining("\t\n")));
                assertEquals("Failed for file: " + fileName, archetype, document.getArchetype());
            }
        }
    }

}
