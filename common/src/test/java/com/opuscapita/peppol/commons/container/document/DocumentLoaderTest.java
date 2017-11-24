package com.opuscapita.peppol.commons.container.document;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentLoaderTest {
    @Autowired
    private DocumentLoader loader;

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
                "/invalid/austria_files/invalids1.xml",
                "/invalid/austria_files/invalids2.xml",
                "/invalid/austria_files/invalids3.xml",
                "/invalid/austria_files/invalids4.xml",
                "/invalid/austria_files/invalids5.xml"
        );
        checkTypes(list, Archetype.PEPPOL_BIS);

        list = Arrays.asList(
                "/valid/simpler_invoicing_files/Valid1.xml",
                "/valid/simpler_invoicing_files/Valid2.xml",
                "/valid/simpler_invoicing_files/Valid3.xml",
                "/valid/simpler_invoicing_files/Valid4.xml",
                "/valid/simpler_invoicing_files/Valid5.xml",
                "/invalid/simpler_invoicing_files/SI-inv-v1.1.3-Valid-version.xml.no.sbdh.sad.smiley",
                "/invalid/simpler_invoicing_files/invalids1.xml",
                "/invalid/simpler_invoicing_files/invalids2.xml",
                "/invalid/simpler_invoicing_files/invalids3.xml",
                "/invalid/simpler_invoicing_files/invalids4.xml",
                "/invalid/simpler_invoicing_files/invalids5.xml",
                "/valid/simpler_invoicing_files/wrapped.SI-UBL-PO-1.2-ok-minimal.xml"
        );
        checkTypes(list, Archetype.PEPPOL_SI);

        list = Arrays.asList(
                "/valid/ehf.xml",
                "/valid/many_names.xml",
                "/valid/many_names_2.xml",
                "/valid/valid_sbdh_2.1.xml",
                "/valid/ehf_2.0_bii4_no.xml",
                "/valid/ehf_2.0_bii5_no.xml",
                "/valid/D.data_mikucto1_OCRITM0231473.xml",
                "/valid/D.data_mikucto1_OCRITM0231473 - no-SBDH.xml"
        );
        checkTypes(list, Archetype.EHF);

        list = Arrays.asList(
                "/valid/svefaktura1.xml",
                "/valid/sv1_with_attachment.xml"
        );
        checkTypes(list, Archetype.SVEFAKTURA1);

        list = Arrays.asList(
                "/invalid/random.xml",
                "/invalid/unrecognized_document_type.xml"
        );
        checkTypes(list, Archetype.UNRECOGNIZED);

        list = Arrays.asList(
                "/invalid/not_xml.txt",
                "/invalid/ehf_no_issue_date.xml"
        );
        checkTypes(list, Archetype.INVALID);

    }

    private void checkTypes(List<String> list, Archetype archetype) throws Exception {
        for (String fileName : list) {
            try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream(fileName)) {
                DocumentInfo document = loader.load(inputStream, fileName, Endpoint.TEST);
                System.out.println(document.getErrors().stream().map(DocumentError::getMessage).collect(Collectors.joining("\t\n")));
                assertEquals("Failed for file: " + fileName, archetype, document.getArchetype());
            }
        }
    }

}
