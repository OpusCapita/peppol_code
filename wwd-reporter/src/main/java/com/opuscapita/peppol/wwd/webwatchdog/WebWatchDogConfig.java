package com.opuscapita.peppol.wwd.webwatchdog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Daniil on 05.07.2016.
 */
public class WebWatchDogConfig {

    private final static Logger logger = LoggerFactory.getLogger(WebWatchDogConfig.class);

    private String folder;
    private String prefix;

    public WebWatchDogConfig() {
    }

    public WebWatchDogConfig(String folder, String prefix) {
        this.folder = folder;
        this.prefix = prefix;
        logger.info("Created Web Watch Dog config: " + this);
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "WebWatchDogConfig{" +
                "folder='" + folder + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }
}
