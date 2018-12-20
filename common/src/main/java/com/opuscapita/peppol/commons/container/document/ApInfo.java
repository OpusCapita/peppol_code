package com.opuscapita.peppol.commons.container.document;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by bambr on 17.12.7.
 */
public class ApInfo {
    private String id;
    private String name;
    private String country;

    private ApInfo(String id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public static ApInfo parseFromCommonName(String commonName) {
        String id = null;
        String name = null;
        String country = null;
        String[] parts = commonName.split(",");
        if (parts.length == 1) {
            id = commonName;
        } else {
            for (String part : parts) {
                String[] keyValue = part.split("=");
                switch (keyValue[0]) {
                    case "CN":
                        id = getKeyValue(keyValue);
                        break;
                    case "O":
                        name = getKeyValue(keyValue);
                        break;
                    case "C":
                        country = getKeyValue(keyValue);
                        break;
                }

            }
        }

        return new ApInfo(id, name, country);
    }

    private static String getKeyValue(String[] keyValue) {
        if (keyValue.length > 1 && StringUtils.isNotBlank(keyValue[1])) {
            return keyValue[1];
        }
        return "";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name == null ? id : name;
    }

    public String getCountry() {
        return country;
    }
}
