package com.opuscapita.peppol.commons.events;

import org.junit.Test;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class EventingMessageUtilTest {
    @Test
    public void extractBaseName() throws Exception {
        String fixture = "/peppol/data/tmp/20171102/D.55007-BEL5F81EE14BF6411E79E11C7648F75FDC7.xml";
        String expected = "D.55007-BEL5F81EE14BF6411E79E11C7648F75FDC7.xml";
        String actual = EventingMessageUtil.extractBaseName(fixture);
        assertEquals(expected, actual);
    }

    @Test
    public void AttemptsCompareTest(){
        SortedSet<Attempt> attempts = new TreeSet<>();

        Attempt one = new Attempt("1501345069079_/peppol/data/tmp/20171122/D.53770-BEL90CA5CB0CF5211E7A3EA5BF933EB8962.xml_75634a7e-a426-40ea-9d43-58e76766ce2e",null,"test_fileName.whatever");
        Attempt two = new Attempt("1501345069230_/peppol/data/tmp/20171122/D.53770-BEL90FC9054CF5211E784E11BEDAA623A9D.xml_f9b69bb8-23d1-4987-8ce2-320e1252dcd9",null,"test_fileName.whatever");

        attempts.add(one);
        attempts.add(two);
        assertEquals(attempts.last(), two);

        Attempt three = new Attempt("1511345060000_/peppol/data/tmp/20171122/D.53770-BEL90FC9054CF5211E784E11BEDAA623A9D.xml_f9b69bb8-",null,"test");
        attempts.add(three);
        assertEquals(attempts.last(), three);

        Attempt four = new Attempt(System.currentTimeMillis() + "_" + "random" , null, "test");
        attempts.add(four);
        assertEquals(attempts.last(), four);

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Attempt five = new Attempt(System.currentTimeMillis() + "_" + "random" , null, "test");
        attempts.add(five);

        assertEquals(attempts.last(),five);

    }

}