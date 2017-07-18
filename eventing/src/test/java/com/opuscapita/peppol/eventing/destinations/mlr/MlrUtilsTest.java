package com.opuscapita.peppol.eventing.destinations.mlr;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class MlrUtilsTest {

    @Test
    public void convertToXml() throws Exception {
        String date = "2017-07-18";
        assertTrue(MlrUtils.convertToXml(date).toString().contains("2017-07-18"));
    }

}