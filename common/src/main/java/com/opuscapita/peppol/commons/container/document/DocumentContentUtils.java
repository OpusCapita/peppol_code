package com.opuscapita.peppol.commons.container.document;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by bambr on 16.3.10.
 */
public class DocumentContentUtils {
    private static final long INTERVAL = 1000l;
    private static final long ALLOWED_DURATION = 30000l;


    public static byte[] getDocumentBytes(Document document) throws TransformerException, InterruptedException, ExecutionException, TimeoutException {
        DOMSource source = new DOMSource(document);

        CompletableFuture<byte[]> thingie = CompletableFuture.supplyAsync(() -> new ThreadedTransformer().getDocumentBytes(false, source));
        return thingie.get(ALLOWED_DURATION, TimeUnit.MILLISECONDS);
    }

    public static byte[] nodeToByteArray(Node node) throws TransformerException, InterruptedException, ExecutionException, TimeoutException {
        DOMSource domSource = new DOMSource(node);
        CompletableFuture<byte[]> thingie = CompletableFuture.supplyAsync(() -> new ThreadedTransformer().getDocumentBytes(true, domSource));
        return thingie.get(ALLOWED_DURATION, TimeUnit.MILLISECONDS);
    }

}
