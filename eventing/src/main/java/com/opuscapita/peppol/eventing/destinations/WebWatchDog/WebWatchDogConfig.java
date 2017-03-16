package com.opuscapita.peppol.eventing.destinations.WebWatchDog;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Daniil on 05.07.2016.
 */
@ConfigurationProperties(prefix = "wwd")
public class WebWatchDogConfig {

    private String folder;
    private String prefix;

    public WebWatchDogConfig() {
    }

    public WebWatchDogConfig(String folder, String prefix) {
        this.folder = folder;
        this.prefix = prefix;
    }

    public String getFolder() {
        return folder;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setFolder(String folder) {
        this.folder = folder;
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
