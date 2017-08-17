package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * Created by bambr on 17.21.2.
 */
public class TestCommon {
    protected void runTestsOnDocumentProfile(String documentProfile, Consumer<? super File> consumer) throws UnsupportedEncodingException {
        File resourceDir = new File(this.getClass().getResource("/test_data/" + documentProfile + "_files").getFile());
        String resourcePath = URLDecoder.decode(resourceDir.getAbsolutePath(), "UTF-8");
        System.out.println(resourceDir.getAbsolutePath() + " exists: " + resourceDir.exists());
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(resourcePath), (path) -> {
                boolean result = false;
                try {
                    System.out.println(path.toFile().getAbsolutePath() + " -> " + path.getFileName().toString().toLowerCase().endsWith("xml"));
                    result = path.getFileName().toString().toLowerCase().endsWith("xml");
                } catch (Exception e) {
                }
                return result;
            });
        /*Arrays.asList(resourceDir.list()).forEach(System.out::println);
        String[] dataFiles = resourceDir.list((dir, name) -> {
                    System.out.println(name + " -> " + name.toLowerCase().endsWith("xml"));
            return name.toLowerCase().endsWith("xml");
        }
        );*/
            StreamSupport.stream(directoryStream.spliterator(), false).map(path -> {
                System.out.println(path.toFile().getAbsolutePath());
                return path.toFile();
            }).filter(fileToCheck -> fileToCheck.isFile() && fileToCheck.exists()).forEach(consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
