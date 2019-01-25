package com.opuscapita.peppol.commons.container.document;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by bambr on 17.12.7.
 */
public class ApInfo {
    private String id;
    private String name;
    private String subject;
    private String country;

    private ApInfo(String id) {
        this(id, null, null, null);
    }

    private ApInfo(String id, String name, String subject, String country) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.country = country;
    }

    public static ApInfo parseFromCommonName(String commonName) {
        if (StringUtils.isBlank(commonName)) {
            return null;
        }
        
        String[] parts = commonName.trim().split(",");
        if (parts.length == 1) {
            return new ApInfo(commonName);
        }

        String id = null;
        String name = null;
        String subject = null;
        String country = null;
        for (String part : parts) {
            String[] keyValue = part.split("=");
            switch (keyValue[0].trim()) {
                case "CN":
                    id = getKeyValue(keyValue);
                    break;
                case "O":
                    name = getKeyValue(keyValue);
                    break;
                case "OU":
                    subject = getKeyValue(keyValue);
                    break;
                case "C":
                    country = getKeyValue(keyValue);
                    break;
            }
        }

        return new ApInfo(id, name, subject, country);
    }

    private static String getKeyValue(String[] keyValue) {
        if (keyValue.length > 1 && StringUtils.isNotBlank(keyValue[1].trim())) {
            return keyValue[1].trim();
        }
        return "";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name == null ? getId() : name;
    }

    public String getSubject() {
        return subject == null ? getName() : subject;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "C=" + country + ", O=" + name + ", OU=" + subject + ", CN=" + id;
    }
}
