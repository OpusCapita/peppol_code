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
import java.util.stream.Collectors;
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

    public byte[] findMessage(String fileName) throws Exception{
        return new FileFinder(directoryList).find(fileName);
    }

}

class FileFinder {
    private final List<String> directoryList;
    private byte[] result = null;

    public FileFinder(List<String> directoryList) throws Exception {
        this.directoryList = directoryList;
        if (directoryList == null || directoryList.isEmpty())
            throw new Exception("Directory list null or empty!");
    }

    public byte[] find(String fileName) throws FileNotFoundException {
        for (String dir : directoryList){
            find(fileName, new File(dir));
            if(result != null)
                return result;
        }
        if (result == null)
            throw new FileNotFoundException("File: " + fileName + " not found!");
        return result;
    }

    //Recursion
    private void find(String fileName, File directory) {
        try {
            File file = new File(directory + File.separator + fileName);
            if (file.exists()) {
                try (InputStream is = new FileInputStream(file)) {
                    result = IOUtils.toByteArray(is);
                    return;
                }
            }

            File fileGz = new File(directory + File.separator + fileName + ".gz");
            if (fileGz.exists()) {
                try (GZIPInputStream is = new GZIPInputStream(new FileInputStream(fileGz))) {
                    result = IOUtils.toByteArray(is);
                    return;
                }
            }

            List<String> subDirectories = Arrays.stream(directory.listFiles(File::isDirectory))
                    .map(x -> x.getAbsolutePath()).collect(Collectors.toList());

            if (subDirectories != null && !subDirectories.isEmpty())
                for(String dir : subDirectories) {
                    find(fileName, new File(dir));
                    if(result != null)                      //stop the recursion
                        return;
                }

        } catch (Exception ex) {
        }
    }
}
