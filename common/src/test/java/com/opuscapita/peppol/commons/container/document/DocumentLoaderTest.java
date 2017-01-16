package com.opuscapita.peppol.commons.container.document;

import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.document.impl.SveFaktura1Document;
import com.opuscapita.peppol.commons.container.document.impl.UblDocument;
import com.opuscapita.peppol.commons.container.document.test.TestTypeOne;
import com.opuscapita.peppol.commons.container.document.test.TestTypeTwo;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
public class DocumentLoaderTest {
    private final DocumentLoader loader = new DocumentLoader();

    @Test
    public void testDocumentLoader() throws Exception {
        Set<BaseDocument> result = DocumentLoader.reloadDocumentTypes("com.opuscapita.peppol.commons.container.document.test");
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.toArray()[0].getClass() == TestTypeOne.class ||
                result.toArray()[0].getClass() == TestTypeTwo.class);
        assertTrue(result.toArray()[1].getClass() == TestTypeOne.class ||
                result.toArray()[1].getClass() == TestTypeTwo.class);
    }

    @Test
    public void testTypes() throws Exception {
        List<String> list;

        // test single document using this code
        // list = Arrays.asList("/invalid/simpler_invoicing_files/invalids5.xml");
        // checkTypes(list, UblDocument.class);

        list = Arrays.asList(
                "/valid/ehf.xml",
                "/valid/ehf_2.0_bii4_no.xml",
                "/valid/ehf_2.0_bii5_no.xml",
                "/valid/valid.german.xml",
                "/valid/valid_sbdh_2.1.xml",
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
        checkTypes(list, UblDocument.class);

        list = Arrays.asList(
                "/valid/svefaktura1.xml",
                "/valid/sv1_with_attachment.xml"
        );
        checkTypes(list, SveFaktura1Document.class);

        list = Arrays.asList(
                "/invalid/not_xml.txt",
                "/invalid/random.xml"
        );
        checkTypes(list, InvalidDocument.class);

    }

    private void checkTypes(List<String> list, Class<? extends BaseDocument> documentClass) throws Exception {
        for (String fileName : list) {
            try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream(fileName)) {
                BaseDocument document = loader.load(inputStream, "test");
                assertTrue("Failed for file " + fileName, document.getClass() == documentClass);
            }
        }
    }

}
