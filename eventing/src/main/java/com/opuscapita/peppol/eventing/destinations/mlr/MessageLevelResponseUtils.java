package com.opuscapita.peppol.eventing.destinations.mlr;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Sergejs.Roze
 */
class MessageLevelResponseUtils {

    /**
     * Receives date in format yyyy-MM-dd and returns date in Peppol expected format.
     *
     * @param date the issue date in yyyy-MM-dd format
     *
     * @return the issue date to be used in document composition
     *
     * @throws ParseException failed to parse input date
     * @throws DatatypeConfigurationException failed to configure XML parser
     */
    static XMLGregorianCalendar convertToXml(String date) throws ParseException, DatatypeConfigurationException {
        return convertToXml(new SimpleDateFormat("yyyy-MM-dd").parse(date));
    }

    static XMLGregorianCalendar convertToXmlTime(String time) throws ParseException, DatatypeConfigurationException {
        return convertToXml(new SimpleDateFormat("HH:mm:ss").parse(time));
    }

    static XMLGregorianCalendar convertToXml(Date date) throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

}
