package com.opuscapita.peppol.commons.container.xml;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application.yml")
public class DocumentTemplatesTest {
    @Autowired
    private DocumentTemplates documentTemplates;

    @Test
    public void testLoader() {
        assertNotNull(documentTemplates);
        List<DocumentTemplate> result = documentTemplates.getTemplates();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        //TODO there are only several document types, need to add more
        assertEquals(2, result.stream().map(DocumentTemplate::getName).filter("SVEFAKTURA1.Invoice"::equals).count());
        assertEquals(2, result.stream().map(DocumentTemplate::getName).filter("PEPPOL_BIS.Invoice"::equals).count());
        assertEquals(2, result.stream().map(DocumentTemplate::getName).filter("EHF.Invoice"::equals).count());
        assertEquals(2, result.stream().map(DocumentTemplate::getName).filter("PEPPOL_BIS.CreditNote"::equals).count());
        assertEquals(2, result.stream().map(DocumentTemplate::getName).filter("EHF.CreditNote"::equals).count());
    }
}
