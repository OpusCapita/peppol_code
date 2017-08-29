package com.opuscapita.peppol.support.ui.common;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private static final Logger logger = Logger.getLogger(FileFinder.class);

    public FileFinder(List<String> directoryList) throws Exception {
        this.directoryList = directoryList;
        if (directoryList == null || directoryList.isEmpty())
            throw new Exception("Directory list null or empty!");
    }

    public byte[] find(String fileName) throws FileNotFoundException {
        File f = new File(fileName);
        //checking if file already has full path and can be found on this step
        if(f.exists())
            return convertFileToByteArray(f);
        //clearing the path if any and leaving just simple name
        logger.warn("WARNING! " + fileName + " not found by full path, checking predefined directories");
        fileName = f.getName();
        for (String dir : directoryList){
            f = new File(dir + File.separator + fileName);
            if (f.exists()) {
                return convertFileToByteArray(f);
            }

        }
        throw new FileNotFoundException("File: " + fileName + " not found!");
    }

    private byte[] convertFileToByteArray(File file) {
        try {
            try (InputStream is = new FileInputStream(file)) {
                return IOUtils.toByteArray(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
