package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class FileConsumer extends Consumer {
    private final static Logger logger = LoggerFactory.getLogger(FileConsumer.class);
    protected final String expectedValue;
    protected int delay = 5000;
    protected File file;
    protected TestResult result;
    protected File currentDirectory;

    public FileConsumer(String id, String name, String expectedValue, Integer delay) {
        super(id);
        this.name = name;
        this.expectedValue = expectedValue;
        if(delay != null)
            this.delay = delay;
    }

    @Override
    public TestResult consume(Object consumable) {
        initCurrentDirectory(consumable);
        file = new File(currentDirectory, expectedValue);

        if(file == null) {
            return result;
        }
        if(!file.exists()) {
            logger.warn("FileConsumer: no files to consume in " + file.getAbsolutePath() + " retry in: " + delay);
            listFoundFiles();
            waitFixedDelay();
        }
        if(file.exists()) {
            clean();
            return new TestResult(name, true, "Found expected file " + file.getAbsolutePath());
        }
        return result;
    }

    // list whatever there for debugging purposes
    private void listFoundFiles() {
        logger.info("Listing the files in the directory...");
        Date earlier = DateUtils.addSeconds(new Date(), -1200);
        AgeFileFilter ageFileFilter = new AgeFileFilter(earlier);
        Iterator<File> files = FileUtils.iterateFiles(currentDirectory, ageFileFilter, null);
        while (files.hasNext()) {
            File file = files.next();
            logger.info("Instead found this file: " + file.getAbsolutePath());
        }
    }

    protected void initCurrentDirectory(Object consumable) {
        if(consumable == null) {
            result = new TestResult(name, false, "FileConsumer: Invalid consumable, null or empty!");
            return;
        }

        currentDirectory = (File) consumable;
        if(!currentDirectory.isDirectory()) {
            result = new TestResult(name, false, "FileConsumer: Directory not found " + currentDirectory);
            return;
        }
        result = new TestResult(name, false, "FileConsumer: expected file: " + expectedValue + " not found in: " + currentDirectory);
    }

    protected boolean clean(){
        return file.delete();
    }

    protected void waitFixedDelay() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
