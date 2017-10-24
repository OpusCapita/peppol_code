package com.opuscapita.peppol.mlr.util;

import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class MessageLevelResponseUtilsTest {

    @Test
    public void convertToXmlDate() throws Exception {
        String date = "2017-07-18";
        XMLGregorianCalendar convertedDate = MessageLevelResponseUtils.convertToXml(date);
        assertTrue(convertedDate.toString().contains("2017-07-18"));
    }

    @Test
    public void convertToXmlTime() throws Exception {
        String time = "11:57:14";
        assertTrue(MessageLevelResponseUtils.convertToXmlTime(time).toString().contains("11:57:14"));
    }

    @Test
    public void testConvertDateToXml() throws Exception {
        String date = "2017-07-18";
        String result = MessageLevelResponseUtils.convertDateToXml(date);
        System.out.println(result);
        assertTrue(result.contains("2017-07-18"));
    }

    @Test
    public void testConvertTimeToXml() throws Exception {
        String time = "11:57:14";
        String result = MessageLevelResponseUtils.convertTimeToXml(time);
        assertTrue(result.contains("11:57:14"));
    }

}