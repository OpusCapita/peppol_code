package com.opuscapita.peppol.support.ui.common;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.18.2
 * Time: 10:32
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Util {

    @Value("${download.dir.list}")
    protected String downloadDirectoryList;
    @Autowired
    Environment environment;
    private List<String> directoryList;

    @PostConstruct
    public void init() {
        directoryList = new ArrayList<>(Arrays.asList(downloadDirectoryList.split(";")));
    }

    public byte[] findMessage(String fileName) throws FileNotFoundException {
        try {
            for (String directory : directoryList) {
                File file = new File(directory + File.separator + fileName);
                if (file.exists()) {
                    try (InputStream is = new FileInputStream(file)) {
                        return IOUtils.toByteArray(is);
                    }
                }
                File fileGz = new File(directory + File.separator + fileName + ".gz");
                if (fileGz.exists()) {
                    try (GZIPInputStream is = new GZIPInputStream(new FileInputStream(fileGz))) {
                        return IOUtils.toByteArray(is);
                    }
                }

            }

        } catch (Exception pass) {

        }
        throw new FileNotFoundException("File: " + fileName + " not found!");
    }
}
