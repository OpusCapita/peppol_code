package com.opuscapita.peppol.validator.xslt;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * @author Sergejs.Roze
 */
public class XsltTransformerTest {

    public static void main(String[] args) throws TransformerException, FileNotFoundException, UnsupportedEncodingException {
        //System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
//        if (args.length != 2) {
//            System.err.println("Usage: $me xslt-file input-file");
//            System.exit(0);
//        }

        File xsltFile = new File("/home/rozeser1/Downloads/nodifi/new/xsl/NOGOV-UBL-T10.xsl");

        File inputFile = new File("/home/rozeser1/Downloads/532c79be-8738-4b35-8257-b9efacb1e7fe.xml");
//        File inputFile = new File("/home/rozeser1/Downloads/D.24425-BELE7058DFC86EC11E6BEC27F0C2BE60F23.xml"); // fails
//        File inputFile = new File("/home/rozeser1/Downloads/D.56980-BEL2449A5F29E6311E7A4D3371AB1B8DE82.xml"); // good

        TransformerFactory tf = TransformerFactory.newInstance(
        //        "net.sf.saxon.TransformerFactoryImpl", null
        );
        Source xslt = new StreamSource(new FileInputStream(xsltFile));
        Transformer transformer = tf.newTransformer(xslt);

        Source input = new StreamSource(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
        transformer.transform(input, new StreamResult(System.out));

//        System.out.println("0-----------------------------------------------------------------------");
//
//        input = new StreamSource(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
//        transformer.transform(input, new StreamResult(System.out));
    }

}
