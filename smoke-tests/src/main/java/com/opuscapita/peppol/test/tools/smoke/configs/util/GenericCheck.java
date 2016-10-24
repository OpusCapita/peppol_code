package com.opuscapita.peppol.test.tools.smoke.configs.util;

import java.util.Map;

/**
 * Created by gamanse1 on 2016.10.20..
 */
@Deprecated
public class GenericCheck {
    String name;
    String type;
    Map<String, String> params;

    public GenericCheck(String name, String type, Map<String, String> params) {
        this.name = name;
        this.type = type;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
