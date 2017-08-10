package com.opuscapita.peppol.eventing.destinations.mlr;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class MessageLevelResponseUtilsTest {

    @Test
    public void convertToXmlDate() throws Exception {
        String date = "2017-07-18";
        assertTrue(MessageLevelResponseUtils.convertToXml(date).toString().contains("2017-07-18"));
    }

    @Test
    public void convertToXmlTime() throws Exception {
        String time = "11:57:14";
        assertTrue(MessageLevelResponseUtils.convertToXmlTime(time).toString().contains("11:57:14"));
    }

}