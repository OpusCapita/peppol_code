package com.opuscapita.peppol.commons.mq;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Sergejs.Roze
 */
public class ConnectionStringTest {

    @Test
    public void testParsing() throws Exception {
        ConnectionString cs = new ConnectionString("test");
        assertEquals("test", cs.getQueue());
        assertNull(cs.getExchange());
        assertEquals(0, cs.getXDelay());

        cs = new ConnectionString("queue:x-delay=100,exchange=test");
        assertEquals("queue", cs.getQueue());
        assertEquals(100, cs.getXDelay());
        assertEquals("test", cs.getExchange());

        cs = new ConnectionString("queue:");
        assertEquals("queue", cs.getQueue());
        assertEquals(0, cs.getXDelay());
        assertNull(cs.getExchange());
    }

}