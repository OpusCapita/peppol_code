package com.opuscapita.peppol.inbound;

import no.difi.oxalis.api.persist.PayloadPersister;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ServiceLoader;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
public class LoaderTest {

    @Ignore("Properties file moved from resource to local file, no idea how to make it available for unit test")
    @Test
    public void testLoader() {
        System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        ServiceLoader<PayloadPersister> serviceLoader = createCustomServiceLoader("file:///main/");

        assertNotNull(serviceLoader);
        assertTrue(serviceLoader.iterator().hasNext());
        assertEquals(InboundReceiver.class, serviceLoader.iterator().next().getClass());
    }

    // code block from Oxalis library that loads custom persistence
    @SuppressWarnings("SameParameterValue")
    private static ServiceLoader<PayloadPersister> createCustomServiceLoader(String persistenceClassPath) {
        if (persistenceClassPath == null || persistenceClassPath.trim().length() == 0) {
            throw new IllegalArgumentException("persistence class path null or empty");
        }
        if (!persistenceClassPath.endsWith("/") && !persistenceClassPath.endsWith(".jar")) {
            throw new IllegalStateException("Invalid class path: " + persistenceClassPath + " ; must end with either / or .jar");
        }

        try {
            URL classPathUrl = new URL(persistenceClassPath);
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{classPathUrl}, Thread.currentThread().getContextClassLoader());
            System.out.println("Custom class loader: " + Arrays.toString(urlClassLoader.getURLs()));

            System.out.println("Searching for " + "META-INF/services/" + PayloadPersister.class.getName());
            URL r = urlClassLoader.getResource("META-INF/services/" + PayloadPersister.class.getName());
            if (r == null) {
                System.out.println("No META-INF/services file found for " + PayloadPersister.class.getName());
            }

            ServiceLoader<PayloadPersister> serviceLoader = ServiceLoader.load(PayloadPersister.class, urlClassLoader);
            System.out.println("Loading MessageRepository instances from " + classPathUrl.toExternalForm());
            return serviceLoader;
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unable to establish class loader for path " + persistenceClassPath + "; " + e, e);
        }
    }
}

