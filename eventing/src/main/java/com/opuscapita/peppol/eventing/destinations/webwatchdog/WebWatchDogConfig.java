package com.opuscapita.peppol.eventing.destinations.webwatchdog;

/**
 * Created by Daniil on 05.07.2016.
 */
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
