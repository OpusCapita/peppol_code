package com.opuscapita.peppol.inbound;

import eu.peppol.persistence.MessageRepository;
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

    @Test
    public void testLoader() throws Exception {
        System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        ServiceLoader<MessageRepository> serviceLoader = createCustomServiceLoader("file:///main/");

        assertNotNull(serviceLoader);
        assertTrue(serviceLoader.iterator().hasNext());
        assertEquals(CustomMessageRepository.class, serviceLoader.iterator().next().getClass());
    }

    // code block from Oxalis library that loads custom persistence
    private static ServiceLoader<MessageRepository> createCustomServiceLoader(String persistenceClassPath) {
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

            System.out.println("Searching for " + "META-INF/services/" + MessageRepository.class.getName());
            URL r = urlClassLoader.getResource("META-INF/services/" + MessageRepository.class.getName());
            if (r == null) {
                System.out.println("No META-INF/services file found for " + MessageRepository.class.getName());
            }

            ServiceLoader<MessageRepository> serviceLoader = ServiceLoader.load(MessageRepository.class, urlClassLoader);
            System.out.println("Loading MessageRepository instances from " + classPathUrl.toExternalForm());
            return serviceLoader;
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unable to establish class loader for path " + persistenceClassPath + "; " + e, e);
        }
    }
}

