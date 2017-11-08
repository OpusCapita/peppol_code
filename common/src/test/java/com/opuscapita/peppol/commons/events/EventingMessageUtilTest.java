package com.opuscapita.peppol.commons.events;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventingMessageUtilTest {
    @Test
    public void extractBaseName() throws Exception {
        String fixture = "/peppol/data/tmp/20171102/D.55007-BEL5F81EE14BF6411E79E11C7648F75FDC7.xml";
        String expected = "D.55007-BEL5F81EE14BF6411E79E11C7648F75FDC7.xml";
        String actual = EventingMessageUtil.extractBaseName(fixture);
        assertEquals(expected, actual);
    }

}